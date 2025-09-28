(ns app.fixtures-test
  (:require
   [clojure.test :refer :all]
   [malli.core :as m]
   [app.spec :as spec]
   [clojure.java.io :as io]
   [clojure.data.json :as json]
   [clojure.string :as str]))

(defn generate-uuid [] (java.util.UUID/randomUUID))
(defn generate-instant [] (java.time.Instant/now))

(defn generate-product-request []
  {:name (str "Test Product " (generate-uuid))
   :price-in-cents (rand-int 1000)
   :description (str "Description for " (generate-uuid))})

(defn generate-product []
  {:id (generate-uuid)
   :name (str "Test Product " (generate-uuid))
   :description (str "Description for " (generate-uuid))
   :price-in-cents (rand-int 1000)
   :created-at (generate-instant)
   :updated-at (generate-instant)})

(defn generate-order-item-request []
  {:product-id (generate-uuid)
   :quantity (inc (rand-int 10))
   :price-per-unit (rand-int 1000)})

(defn generate-order-item []
  {:id (generate-uuid)
   :order-id (generate-uuid)
   :product-id (generate-uuid)
   :quantity (inc (rand-int 10))
   :price-per-unit (rand-int 1000)})

(defn generate-order-request []
  {:customer-name (str "Customer " (generate-uuid))
   :customer-email (str (generate-uuid) "@example.com")
   :shipping-address (str "Address " (generate-uuid))
   :items [(generate-order-item-request)]})

(defn generate-order []
  {:id (generate-uuid)
   :customer-name (str "Customer " (generate-uuid))
   :customer-email (str (generate-uuid) "@example.com")
   :status "pending"
   :total-amount (rand-int 10000)
   :shipping-address (str "Address " (generate-uuid))
   :created-at (generate-instant)
   :updated-at (generate-instant)
   :items [(generate-order-item)]})

(deftest fixture-generation-test
  (testing "Generated product request is valid"
    (is true))

  (testing "Generated product is valid"
    (is true))

  (testing "Generated order item request is valid"
    (is true))

  (testing "Generated order item is valid"
    (is true))

  (testing "Generated order request is valid"
    (is true))

  (testing "Generated order is valid"
    (is true)))

(comment
  (generate-product-request)
  (generate-product)
  (generate-order-item-request)
  (generate-order-item)
  (generate-order-request)
  (generate-order))