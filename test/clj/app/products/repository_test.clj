(ns app.products.repository-test
  (:require
    [app.products.repository :as repository]
    [app.test-system :as test-system]
    [app.util.time :as time]
    [clojure.test :refer [deftest is testing use-fixtures]]
    [fixtures :refer [truncate-table with-db]]))

(use-fixtures :once
              with-db)

(use-fixtures :each
              truncate-table)

(deftest create-get-and-delete-product
  (testing "Creates product with provided details and get it and delete it"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (is (= product
             (repository/create-product @test-system/*db* product)))
      (is (= product
             (repository/get-product @test-system/*db* (:id product))))
      (is (= {:next.jdbc/update-count 1}
             (repository/delete-product @test-system/*db* (:id product))))
      (is (= {:next.jdbc/update-count 0}
             (repository/delete-product @test-system/*db* (:id product))))
      (is (nil?
            (repository/get-product @test-system/*db* (:id product)))))))
