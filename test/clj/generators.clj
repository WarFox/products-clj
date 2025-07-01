(ns generators
  (:require
   [app.spec :as spec]
   [app.util.time :as time]
   [malli.core :as malli]
   [malli.generator :as mg]))

(defn generate-order
  "Generates a random order with valid data"
  []
  (let [order           (mg/generate spec/OrderV1)
        order-items     (mapv #(assoc % :order-id (:id order))
                              (repeatedly (rand-int 10) #(mg/generate spec/OrderItemV1)))
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
         effective-product (-> product
                               (assoc :id id)
                               (assoc :created-at (time/instant-now :millis))
                               (assoc :updated-at (time/instant-now :millis)))]
     (malli/assert spec/ProductV1 effective-product)
     effective-product)))
