(ns app.orders.domain-test
  (:require
   [clojure.test :refer :all]
   [app.orders.domain :as domain]
   [app.spec :as spec]
   [app.fixtures-test :as fixtures]
   [malli.core :as m]))

(deftest order-item-request->order-item-test
  (testing "Transforms an order item request into a valid order item domain object"
    (let [order-item-request (fixtures/generate-order-item-request)
          order-id (fixtures/generate-uuid)
          order-item (domain/->OrderItem order-id order-item-request)]
      (is (m/validate spec/OrderItemV1 order-item))
      (is (= (:product-id order-item-request) (:product-id order-item)))
      (is (= (:quantity order-item-request) (:quantity order-item)))
      (is (= (:price-per-unit order-item-request) (:price-per-unit order-item)))
      (is (= order-id (:order-id order-item)))
      (is (some? (:id order-item))))))

(deftest order-request->order-test
  (testing "Transforms an order request into a valid order domain object"
    (let [order-request (fixtures/generate-order-request)
          order (domain/->Order order-request)]
      (is (m/validate spec/OrderV1 order))
      (is (= (:customer-name order-request) (:customer-name order)))
      (is (= (:customer-email order-request) (:customer-email order)))
      (is (= (:shipping-address order-request) (:shipping-address order)))
      (is (= "pending" (:status order)))
      (is (some? (:id order)))
      (is (some? (:created-at order)))
      (is (some? (:updated-at order)))
      (is (= (count (:items order-request)) (count (:items order))))
      (is (= (:total-amount order) (reduce + (map #(* (:quantity %) (:price-per-unit %)) (:items order))))))))
