(ns app.util.parse)

(defn parse-uuid-or
  "Parses `this` string to a uuid or returns that if nil or invalid. Returns `this` if it is a uuid"
  [this that]
  (cond
    (nil? this) that
    (uuid? this) this
    (string? this) (try
                     (parse-uuid this)
                     (catch Exception _ that))))
