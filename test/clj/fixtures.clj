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
  (jdbc/execute! @test-system/*db* ["truncate table order_items cascade"])
  (jdbc/execute! @test-system/*db* ["truncate table orders cascade"])
  (jdbc/execute! @test-system/*db* ["truncate table products cascade"])
  (f))

(defn with-system
  [f]
  (let [sys (test-system/init-test-system)]
    (f)
    (system/halt! sys)))
