(ns app.util.time
  (:require
   [cljc.java-time.instant :as instant]
   [cljc.java-time.temporal.chrono-unit :as chrono-unit]))

(defmulti instant-now
  (fn [unit] unit))

(defmethod instant-now :millis
  [_]
  (-> (instant/now)
      (instant/truncated-to chrono-unit/millis)))

(defmethod instant-now :micros
  [_]
  (-> (instant/now)
      (instant/truncated-to chrono-unit/micros)))

(defmethod instant-now :default
  [_]
  (instant/now))

(defn parse-instant
  "Parse java.time.Instant from string"
  [s]
  (instant/parse s))
