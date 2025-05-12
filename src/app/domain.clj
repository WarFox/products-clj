(ns app.domain
  (:require
   [app.util.time :as time]))

(defn parse-inst-or
  "Parses `this` string to an Instant or returns `that` if nil or invalid. Returns `this` if it is an instant"
  [this that]
  (cond
    (nil? this)    that
    (inst? this)   this
    (string? this) (try
                     (time/parse-instant this)
                     (catch Exception _ that))))

(defn parse-uuid-or
  "Parses `this` string to a uuid or returns that if nil or invalid. Returns `this` if it is a uuid"
  [this that]
  (cond
    (nil? this)    that
    (uuid? this)   this
    (string? this) (try
                     (parse-uuid this)
                     (catch Exception _ that))))

(defn ->Product
  [p]
  (let [now         (time/instant-now :micros)
        random-uuid (random-uuid)]
    (-> p
        (update :id #(parse-uuid-or % random-uuid))
        (update :created-at #(parse-inst-or % now))
        (update :updated-at #(parse-inst-or % now)))))

