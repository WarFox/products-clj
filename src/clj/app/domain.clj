(ns app.domain
  (:require
    [app.util.time :as time]))

(defn parse-uuid-or
  "Parses `this` string to a uuid or returns that if nil or invalid. Returns `this` if it is a uuid"
  [this that]
  (cond
    (nil? this) that
    (uuid? this) this
    (string? this) (try
                     (parse-uuid this)
                     (catch Exception _ that))))

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
