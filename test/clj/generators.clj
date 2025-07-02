(ns generators
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
  ([]
   (generate-product (random-uuid)))
  ([id]
   (let [product           (mg/generate spec/ProductV1)
         effective-product (assoc product :id id)]
     (malli/assert spec/ProductV1 effective-product)
     effective-product)))
