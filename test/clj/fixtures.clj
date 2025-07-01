(ns fixtures
  (:require
   [app.test-system :refer [init-db init-test-system db]]
   [app.system :as system]
   [app.orders.repository :as order-repo]
   [app.products.services :as product-service]
   [generators :refer [generate-product]]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as types]
   [next.jdbc :as jdbc]))

(defn with-db
  [f]
  (let [sys (init-db)]
    (f)
    (system/halt! sys)))

(defn truncate-table
  [f]
  (jdbc/execute! (db) ["truncate table order_items cascade"])
  (jdbc/execute! (db) ["truncate table orders cascade"])
  (jdbc/execute! (db) ["truncate table products cascade"])
  (f))

(defn with-system
  [f]
  (let [sys (init-test-system)]
    (f)
    (system/halt! sys)))

(defn given-orders
  [orders]
  (let [effective-orders (map (fn [order]
                                (-> order
                                    (dissoc :items)
                                    (assoc :status (types/as-other (:status order))))) orders)]
    (sql/insert-multi! (db)
                       :orders
                       effective-orders
                       jdbc/unqualified-snake-kebab-opts)))

(defn given-order-items
  [order-items]
  (doseq [item order-items]
    (product-service/create-product
     (db)
     (generate-product (:product-id item))))
  (order-repo/create-order-items (db) order-items))

(defn given-order
  [order-data]
  (sql/insert! (db)
               :orders
               (assoc order-data
                      :status (types/as-other (:status order-data)))
               jdbc/unqualified-snake-kebab-opts))
