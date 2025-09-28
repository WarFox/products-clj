(ns app.generators
  (:require
   [app.spec :as spec]
   [app.util.time :as time]
   [malli.core :as malli]
   [malli.generator :as mg]))

(defn generate-order-item
  "Generates a random order item with valid data"
  [order-id]
  (let [order-item           (mg/generate spec/OrderItemV1)
        effective-order-item (assoc order-item :order-id order-id)]
    (malli/assert spec/OrderItemV1 effective-order-item)
    effective-order-item))

(defn generate-order
  "Generates a random order with valid data"
  []
  (let [order           (mg/generate spec/OrderV1)
        order-items     (repeatedly 1 #(generate-order-item (:id order)))
        now             (time/instant-now :millis)
        effective-order (-> order
                            (assoc :items order-items)
                            (assoc :created-at now)
                            (assoc :updated-at now))]
    (malli/assert spec/OrderV1 effective-order)
    effective-order))

(defn generate-product
  "Generates a random product request (without server-managed fields like timestamps)"
  ([]
   (generate-product (random-uuid)))
  ([id]
   ;; For repository tests that need full product with ID and timestamps
   (let [product (-> (mg/generate spec/ProductV1)
                     (assoc :id id))]
     (malli/assert spec/ProductV1 product)
     product)))

(defn generate-product-request
  "Generates a random product request (for API calls)"
  ([]
   (generate-product-request {}))
  ([{:keys [name description price-in-cents]}]
   (let [product (merge-with #(or %2 %1)
                             (mg/generate spec/ProductV1Request)
                             {:name           name
                              :description    description
                              :price-in-cents price-in-cents})]
     (malli/assert spec/ProductV1Request product)
     product)))
