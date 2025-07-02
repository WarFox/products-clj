(ns app.orders.domain
  (:require
   [app.util.parse :refer [parse-uuid-or]]
   [app.util.time :as time]))

(defn ->OrderItem
  "Transform input into a valid OrderItem, applying the following rules:
   - Ensure UUID for id (use provided id if valid, otherwise generate a new one)
   - Ensure UUID for order-id and product-id
   - Set price-per-unit to 0 if not present
   - Set quantity to 1 if not present

   This function respects existing values if they're valid."
  [order-id oi]
  (-> oi
      (update :id #(parse-uuid-or % (random-uuid)))
      (assoc  :order-id order-id)
      (update :product-id #(parse-uuid-or % %))
      (update :price-per-unit #(or % 0))
      (update :quantity #(or % 1))))

(defn calculate-order-total
  "Calculate the total amount for an order based on the order items"
  [items]
  (reduce + (map #(* (:quantity %) (:price-per-unit %)) items)))

(defn ->Order
  "Transform input into a valid Order, applying the following rules:
   - Ensure UUID for id (use provided id if valid, otherwise generate a new one)
   - Set created-at and updated-at timestamps if not present
   - Calculate total-amount if not present
   - Set default status to 'pending' if not present

   This function respects existing values if they're valid."
  [o]
  (let [now (time/instant-now :micros)
        order (-> o
                  (update :id #(parse-uuid-or % (random-uuid)))
                  (update :created-at #(or % now))
                  (update :updated-at #(or % now))
                  (update :status #(or % "pending"))
                  (update :total-amount #(or % (calculate-order-total (:items o)))))]
    (update order :items #(mapv (partial ->OrderItem (:id order)) %))))
