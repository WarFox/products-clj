(ns app.orders.repository
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as types]))

(defn get-orders
  "Fetches all orders from the database"
  [db]
  (plan/select! db
                [:id :customer_name :customer_email :status :total_amount
                 :shipping_address :created_at :updated_at]
                ["select * from orders"]
                jdbc/unqualified-snake-kebab-opts))

(defn get-order-items
  "Fetches all order items for a specific order"
  [db order-id]
  (plan/select! db
                [:id :order-id :product-id :quantity :price-per-unit]
                ["select * from order_items where order_id = ?" order-id]
                jdbc/unqualified-snake-kebab-opts))

(defn get-order-with-items
  "Fetches an order by ID including its items"
  [db id]
  (jdbc/with-transaction [tx db]
    (let [order (sql/get-by-id tx :orders id jdbc/unqualified-snake-kebab-opts)
          items (when order (get-order-items tx id))]
      (when order
        (assoc order :items items)))))

(defn create-order-items
  "Creates order items for an order"
  [db order-id items]
  (doseq [item items]
    (sql/insert! db
                 :order-items
                 (assoc item :order-id order-id)
                 jdbc/unqualified-snake-kebab-opts)))

(defn create-order
  "Creates a new order with its items in a transaction"
  [db {:keys [items] :as order}]
  (jdbc/with-transaction [tx db]
    (let [order-data (dissoc order :items)
          created-order (sql/insert! tx
                                     :orders
                                     (assoc order-data
                                            :status (types/as-other (:status order-data)))
                                     (assoc jdbc/unqualified-snake-kebab-opts
                                            :suffix "RETURNING *"))]
      (create-order-items tx (:id created-order) items)
      (get-order-with-items tx (:id created-order)))))

(defn get-order
  "Fetches an order by ID from the database"
  [db id]
  (get-order-with-items db id))

(defn delete-order
  "Deletes an order and its items (cascade delete) by ID"
  [db id]
  (sql/delete! db :orders {:id id}))

(defn update-order-status
  "Updates the status of an order by ID"
  [db id status]
  (sql/update! db :orders {:status status} {:id id} jdbc/unqualified-snake-kebab-opts)
  (get-order-with-items db id))
