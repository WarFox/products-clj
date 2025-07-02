(ns app.orders.integration-test
  (:require
   [api-helpers :as api]
   [app.products.repository :as product-repository]
   [app.spec :as spec]
   [app.test-system :as test-system]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-system given-orders-with-items!]]
   [generators :refer [generate-product generate-order]]
   [malli.core :as malli]
   [malli.generator :as mg]))

(use-fixtures :once
  with-system)

(use-fixtures :each
  truncate-table)

(deftest create-order-integration-test
  (testing "Create an order via API"
    (let [order-items           (repeatedly 2 #(mg/generate spec/OrderItemV1Request))
          order-request         (assoc (mg/generate spec/OrderV1Request)
                                       :items order-items)
          product-ids           (mapv :product-id order-items)
          _                     (doseq [product-id product-ids]
                                  (product-repository/create-product (test-system/db) (generate-product product-id)))
          total-amount          (reduce + (map #(* (:quantity %) (:price-per-unit %)) order-items))
          response              (api/create-order! order-request)
          {:keys [status body]} response]
      (malli/assert spec/OrderV1ResponseEnvelope body)
      (is (= 201 status))
      (is (= "success" (:status body)))
      (let [data (:data body)]
        (is (= "pending" (:status data)))
        (is (= total-amount (:totalAmount data)))
        (is (= (count order-items) (count (:items data))))
        (is (= (:createdAt data) (:updatedAt data)))))))

(deftest get-orders-integration-test
  (testing "Get all orders via API"
    (let [orders                (repeatedly 3 generate-order)
          _                     (given-orders-with-items! orders)
          response              (api/get-orders)
          {:keys [status body]} response]
      (is (= 200 status))
      (malli/assert spec/OrderV1ListResponseEnvelope body)
      (is (= "success" (:status body)))
      (let [data (:data body)]
        (is (= 3 (count data)))
        ;; Verify each order has items
        (doseq [order data]
          (is (contains? order :items))
          (is (vector? (:items order)))
          (is (pos? (count (:items order)))))))))

(deftest get-orders-integration-test-empty-case
  (testing "Get all orders via API empty case"
    (let [response              (api/get-orders)
          {:keys [status body]} response]
      (is (= 200 status))
      (malli/assert spec/OrderV1ListResponseEnvelope body)
      (is (= "success" (:status body)))
      (let [data (:data body)]
        (is (vector? data))
        (is (= 0 (count data)))))))

(deftest get-order-by-id-not-found-integration-test
  (testing "Get order by non-existent id returns 404"
    (let [response              (api/get-order (random-uuid))
          {:keys [status]} response]
      (is (= 404 status)))))
