(ns app.products-test
  (:require
   [app.db :as db]
   [app.products :as products]
   [app.server :as server]
   [app.system :as system]
   [app.test-system :as test-system]
   [app.util.time :as time]
   [clj-http.client :as http]
   [clojure.data.json :as json]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [next.jdbc :as jdbc]))

(defn with-system
  [f]
  (let [sys (test-system/init-test-system)]
    (f)
    (system/halt! sys)))

(use-fixtures :once
  with-system)

(defn truncate-table
  [f]
  (jdbc/execute! @test-system/*db* ["truncate table products"])
  (f))

(use-fixtures :each
  truncate-table)

(deftest create-product-test
  (testing "Create a product"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}
          request {:db          @test-system/*db*
                   :body-params product}]
      (is (= {:status 201
              :body   product}
             (products/create-product request))))))

(deftest get-products-test
  (testing "Get all products"
    (let [product {:id             (random-uuid)
                   :name           "Test Product 1"
                   :description    "Hello, this is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (products/create-product {:db          @test-system/*db*
                                :body-params product}) ; Create a product for testing
      (is (= {:status 200
              :body   [product]}
             (products/get-products {:db @test-system/*db*}))))))

(deftest get-product-by-id
  (testing "Get product by id"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (products/create-product {:db          @test-system/*db*
                                :body-params product}) ; Create a product for testing
      (is (= {:status 200
              :body   product}
             (products/get-product {:db          @test-system/*db*
                                    :path-params {:id (.toString (:id product))}}))))))

(deftest post-create-product-test
  (testing "Send POST request to server to create a product"
    (let [product  {:name           "Test Product"
                    :description    "This is a test product"
                    :price-in-cents 100}
          url      (format "http://localhost:%s/v1/products" (server/get-port @test-system/*server*))
          response (http/post url
                              {:form-params  product
                               :content-type :json})
          result   (json/read-str (:body response)
                                  :key-fn keyword)]
      ;; TODO Validate Schema
      (is (= [:id :name :priceInCents :description :createdAt :updatedAt] (keys result)))
      (is (= 201 (:status response)))
      (is (uuid? (parse-uuid (:id result))))
      (is (= (:name product) (:name result)))
      (is (= (:description product) (:description result)))
      (is (= (:price-in-cents product) (:priceInCents result)))
      (is (= (:createdAt result) (:updatedAt result))))))

(deftest get-product-test
  (testing "GET request to server to fetch a product by ID"
    (let [now      (time/instant-now :micros)
          product  (db/create-product @test-system/*db*
                                      {:id             (random-uuid)
                                       :name           "Test Product"
                                       :description    "This is a test product"
                                       :price-in-cents 100
                                       :created-at     now
                                       :updated-at     now})
          url      (format "http://localhost:%s/v1/products/%s" (server/get-port @test-system/*server*) (:id product))
          response (http/get url
                             {:accept :json})
          result   (json/read-str (:body response)
                                  :key-fn keyword)]
      ;; TODO Validate Schema
      ;; (is (malli/validate spec/ProductV1 result)  (-> spec/ProductV1 (malli/explain result) (me/humanize)))
      (is (= [:id :name :priceInCents :description :createdAt :updatedAt] (keys result)))
      (is (= 200 (:status response)))
      (is (= (str (:id product)) (:id result)))
      (is (= (:name product) (:name result)))
      (is (= (:description product) (:description result)))
      (is (= (:price-in-cents product) (:priceInCents result)))
      (is (= (str (:created-at product)) (:createdAt result)))
      (is (= (str (:updated-at product)) (:updatedAt result))))))
