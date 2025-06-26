(ns app.products.domain
  (:require
   [app.util.parse :refer [parse-uuid-or]]
   [app.util.time :as time]))

(defn ->Product
  "Transform input into a valid Product, applying the following rules:
   - Ensure UUID for id (use provided id if valid, otherwise generate a new one)
   - Set created-at and updated-at timestamps if not present

   This function respects existing values if they're valid."
  [p]
  (let [now (time/instant-now :micros)]
    (-> p
        (update :id #(parse-uuid-or % (random-uuid)))
        (update :created-at #(or % now))
        (update :updated-at #(or % now)))))
