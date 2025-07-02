(ns fixtures
  (:require
   [app.orders.repository :as order-repo]
   [app.products.repository :as product-repo]
   [app.system :as system]
   [app.test-system :refer [init-db init-test-system db]]
   [generators :refer [generate-product]]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as types]))

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
    (product-repo/create-product
     (db)
     (generate-product (:product-id item))))
  (order-repo/create-order-items (db) order-items))

(defn given-orders-with-items!
  [orders]
  (given-orders orders)
  (doseq [order orders]
    (given-order-items (:items order))))

(defn given-product
  [product]
  (product-repo/create-product (db) product))
