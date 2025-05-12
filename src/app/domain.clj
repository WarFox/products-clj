(ns app.domain
  (:import
   [java.time Instant]))

(defn parse-inst-or
  "Parses a string to an Instant or returns this if nil or invalid"
  [s this]
  (cond
    (nil? s)    this
    (inst? s)   s
    (string? s) (try
                  (Instant/parse s)
                  (catch Exception _ this))))

(defn parse-uuid-or
  "Parses a string to a uuid or returns this if nil or invalid"
  [s this]
  (cond
    (nil? s)    this
    (uuid? s)   s
    (string? s) (try
                  (parse-uuid s)
                  (catch Exception _ this))))

(defn ->Product
  [p]
  (let [now (Instant/now)
        random-uuid (random-uuid)]
    (-> p
        (update :id #(parse-uuid-or % random-uuid))
        (update :created-at #(parse-inst-or % now))
        (update :updated-at #(parse-inst-or % now)))))

