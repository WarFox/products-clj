(ns app.orders.repository-test
  (:require
   [app.orders.repository :as order-repo]
   [app.spec :as spec]
   [app.test-system :refer [db]]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-db given-orders given-product]]
   [generators :refer [generate-order generate-product]]
   [malli.generator :as mg]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-order-items
  (testing "Create order-items"
    (let [order-id   (random-uuid)
          order-item (-> (mg/generate spec/OrderItemV1)
                         (assoc :id (random-uuid))
                         (assoc :order-id order-id))
          order      (-> (mg/generate spec/OrderV1)
                         (dissoc :items)
                         (assoc :id order-id))
          product    (generate-product (:product-id order-item))]
      (given-product product)
      (given-orders [order])
      (order-repo/create-order-items (db)
                                     [order-item])
      (is (= [order-item]
             (order-repo/get-order-items (db) order-id))))))

(deftest create-get-and-delete-order
  (testing "Creates order with provided details and get it and delete it"
    ;; given products
    (let [order (generate-order)]
      ;; create products for order items
      (doseq [item (:items order)]
        (given-product (generate-product (:product-id item))))
      (is (= order
             (order-repo/create-order-with-items (db)
                                                 order)))
      (is (= order
             (order-repo/get-order (db) (:id order))))
      (is (= {:next.jdbc/update-count 1}
             (order-repo/delete-order (db) (:id order))))
      (is (= {:next.jdbc/update-count 0}
             (order-repo/delete-order (db) (:id order))))
      (is (nil?
           (order-repo/get-order (db) (:id order)))))))

(deftest get-orders-test
  (testing "Get all orders"
    (let [orders (take 2 (repeatedly generate-order))]
      (given-orders orders) ; Create orders for testing
      (is (= (map #(dissoc % :items) orders)
             (order-repo/get-orders (db)))))))
