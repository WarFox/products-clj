(ns app.orders.repository
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as types]
   [integrant.core :as ig]))

(defn get-orders
  "Fetches all orders from the database"
  [db]
  (plan/select! db
                [:id :customer-name :customer-email :status :total-amount
                 :shipping-address :created-at :updated-at]
                ["select * from orders"]
                jdbc/unqualified-snake-kebab-opts))

(defn get-orders-with-items
  "Efficiently fetches all orders with their items in 2 queries to avoid N+1 problem"
  [db]
  (let [orders (get-orders db)
        order-ids (map :id orders)
        all-items (when (seq order-ids)
                    (plan/select! db
                                  [:id :order-id :product-id :quantity :price-per-unit]
                                  [(str "select * from order_items where order_id = ANY(?)")
                                   (into-array java.util.UUID order-ids)]
                                  jdbc/unqualified-snake-kebab-opts))
        items-by-order-id (group-by :order-id all-items)]
    (mapv #(assoc % :items (get items-by-order-id (:id %) []))
          orders)))

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
  [db items]
  (sql/insert-multi! db
                     :order-items
                     items
                     jdbc/unqualified-snake-kebab-opts))

(defn create-order-with-items
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
      (create-order-items tx items)
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

(defmethod ig/init-key :app.orders/repository
  [_ {:keys [db]}]
  {:get-orders             (partial get-orders db)
   :get-orders-with-items  (partial get-orders-with-items db)
   :get-order-items        (partial get-order-items db)
   :get-order-with-items   (partial get-order-with-items db)
   :create-order-items     (partial create-order-items db)
   :create-order-with-items (partial create-order-with-items db)
   :get-order              (partial get-order db)
   :delete-order           (partial delete-order db)
   :update-order-status    (partial update-order-status db)})
