(ns app.db-test
  (:require
   [app.db :as sut]
   [app.util.time :as time]
   [app.test-system :as test-system]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [with-db truncate-table]]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-and-get-product
  (testing "Creates product with provided details and get it"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (is (= product
             (sut/create-product @test-system/*db* product)))
      (is (= product
             (sut/get-product @test-system/*db* (:id product)))))))
