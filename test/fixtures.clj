(ns fixtures
  (:require
   [app.test-system :as test-system]
   [app.system :as system]
   [next.jdbc :as jdbc]))

(defn with-db
  [f]
  (let [sys (test-system/init-db)]
    (f)
    (system/halt! sys)))

(defn truncate-table
  [f]
  (jdbc/execute! @test-system/*db* ["truncate table products"])
  (f))

(defn with-system
  [f]
  (let [sys (test-system/init-test-system)]
    (f)
    (system/halt! sys)))
