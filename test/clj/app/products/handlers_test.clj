(ns app.products.handlers-test
  (:require
   [app.products.repository :as repository]
   [app.products.handlers :as handler]
   [app.spec :as spec]
   [app.test-system :as test-system]
   [app.util.time :as time]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-db]]
   [malli.core :as malli]
   [malli.error :as me]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-product-test
  (testing "Create a product"
    (let [product {:name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100}
          request {:db          @test-system/*db*
                   :body-params product}]
      (is (malli/validate spec/ProductV1Request product) (-> spec/ProductV1Request (malli/explain product) (me/humanize)))
      (let [{:keys [status body]} (handler/create-product request)
            expected              (assoc product
                                         :created-at (:created-at body)
                                         :updated-at (:updated-at body)
                                         :id (:id body))]
        (is (= 201 status))
        (is (= expected body))
        (is (uuid? (:id body)))
        (is (inst? (:created-at body)))
        (is (inst? (:updated-at body)))
        (is (= (:created-at body) (:updated-at body)))))))

(deftest get-products-test
  (testing "Get all products"
    (let [products [{:id             (random-uuid)
                     :name           "Test Product 1"
                     :description    "Hello, this is a test product"
                     :price-in-cents 100
                     :created-at     (time/instant-now :micros)
                     :updated-at     (time/instant-now :micros)}
                    {:id             (random-uuid)
                     :name           "Test Product 2"
                     :description    "Hello, this is a test product 2"
                     :price-in-cents 200
                     :created-at     (time/instant-now :micros)
                     :updated-at     (time/instant-now :micros)}]]
      (doseq [product products]
        (repository/create-product @test-system/*db* product)) ; Create a product for testing
      (is (= {:status 200
              :body   products}
             (handler/list-products {:db @test-system/*db*}))))))

(deftest get-product-by-id
  (testing "Get product by id"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (repository/create-product @test-system/*db* product) ; Create a product for testing
      (is (= {:status 200
              :body   product}
             (handler/get-product {:db          @test-system/*db*
                                   :path-params {:id (str (:id product))}}))))))
