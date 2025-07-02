(ns app.orders.handlers-test
  (:require
   [app.orders.handlers :as order-handler]
   [app.spec :as spec]
   [app.test-system :refer [db log-ds]]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-db given-orders-with-items! given-product]]
   [generators :refer [generate-order generate-product]]
   [malli.generator :as mg]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-order-test
  (testing "Create a order"
    (let [order-items           (repeatedly 2 #(mg/generate  spec/OrderItemV1Request))
          order-request         (assoc (mg/generate spec/OrderV1Request)
                                       :items order-items)
          product-ids           (mapv :product-id order-items)
          _                     (doseq [product-id product-ids]
                                  (given-product (generate-product product-id))) ; Ensure products exist
          request               {:db          (log-ds)
                                 :body-params order-request}
          total-amount          (reduce + (map #(* (:quantity %) (:price-per-unit %)) order-items))
          {:keys [status body]} (order-handler/create-order request)
          expected              (assoc order-request
                                       :created-at (:created-at body)
                                       :updated-at (:updated-at body)
                                       :status "pending"
                                       :total-amount total-amount
                                       :id (:id body))]
      (is (= 201 status))
      (is (= (dissoc expected :items) (dissoc body :items)))
      (is (= (:items expected)  (mapv #(select-keys % [:product-id :quantity :price-per-unit]) (:items body))))
      (is (= (:created-at body) (:updated-at body))))))

(deftest get-orders-test
  (testing "Get all orders"
    (let [orders (repeatedly 3 generate-order)]
      (given-orders-with-items! orders) ; Create orders with items for testing
      (is (= {:status 200
              :body   orders}
             (order-handler/list-orders
              {:db (db)}))))))

(deftest get-order-by-id
  (testing "Get order by id"
    (let [order (generate-order)]
      (given-orders-with-items! [order])
       ; Create a order for testing
      (is (= {:status 200
              :body   order}
             (order-handler/get-order
              {:db          (db)
               :path-params {:id (str (:id order))}}))))))
