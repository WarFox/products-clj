(ns app.orders.repository-test
  (:require
   [app.orders.repository :as order-repo]
   [app.products.services :as product-service]
   [app.spec :as spec]
   [app.test-system :refer [db]]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [clojure.tools.logging :as log]
   [fixtures :refer [truncate-table with-db]]
   [malli.generator :as mg]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.types :as types]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(defn insert-order
  [order-data]
  (log/info "order data ", order-data)
  (let [log-ds (jdbc/with-logging (db) (fn [s v] (log/info s v)))]
    (sql/insert! log-ds
                 :orders
                 (assoc order-data
                        :status (types/as-other (:status order-data)))
                 jdbc/unqualified-snake-kebab-opts)))

(deftest create-order-items
  (testing "Create order-items"
    (let [order-id   (random-uuid)
          order-item (-> (mg/generate spec/OrderItemV1)
                         (assoc :id (random-uuid))
                         (assoc :order-id order-id))
          order      (-> (mg/generate spec/OrderV1)
                         (dissoc :items)
                         (assoc :id order-id))
          product    (assoc (mg/generate spec/ProductV1)
                            :id (:product-id order-item))]
      (product-service/create-product (db) product)
      (insert-order order)
      (order-repo/create-order-items (db)
                                     order-id
                                     [order-item])
      (is (= [order-item]
             (order-repo/get-order-items (db) order-id))))))

(deftest create-get-and-delete-order
  (testing "Creates order with provided details and get it and delete it"
    ;; given products
    (let [order          (mg/generate spec/OrderV1)]
      ;; create products for order items
      (doseq [item (:items order)]
        (product-service/create-product
         (db)
         (-> (mg/generate spec/ProductV1)
             (assoc :id (:product-id item)))))
      (is (= order
             (order-repo/create-order (db)
                                      order)))
      (is (= order
             (order-repo/get-order (db) (:id order))))
      (is (= {:next.jdbc/update-count 1}
             (order-repo/delete-order (db) (:id order))))
      (is (= {:next.jdbc/update-count 0}
             (order-repo/delete-order (db) (:id order))))
      (is (nil?
           (order-repo/get-order (db) (:id order)))))))
