(ns app.products.domain-test
  (:require
   [clojure.test :refer :all]
   [app.products.domain :as domain]
   [app.spec :as spec]
   [app.fixtures-test :as fixtures]
   [malli.core :as m]))

(deftest product-request->product-test
  (testing "Transforms a product request into a valid product domain object"
    (let [product-request (fixtures/generate-product-request)
          product (domain/->Product product-request)]
      (is (m/validate spec/ProductV1 product))
      (is (= (:name product-request) (:name product)))
      (is (= (:description product-request) (:description product)))
      (is (= (:price-in-cents product-request) (:price-in-cents product)))
      (is (some? (:id product)))
      (is (some? (:created-at product)))
      (is (some? (:updated-at product))))))
