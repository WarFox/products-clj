(ns app.products-test
  (:require
   [app.db :as db]
   [app.products :as products]
   [app.server :as server]
   [app.system :as system]
   [app.util.time :as time]
   [app.test-system :as test-system]
   [clj-http.client :as http]
   [clojure.data.json :as json]
   [clojure.test :as t]
   [next.jdbc :as jdbc]))

(defn with-system
  [f]
  (let [sys (test-system/init-test-system)]
    (f)
    (system/halt! sys)))

(t/use-fixtures :once
  with-system)

(defn truncate-table
  [f]
  (jdbc/execute! @test-system/*db* ["truncate table products"])
  (f))

(t/use-fixtures :each
  truncate-table)

(t/deftest create-product-test
  (t/testing "Create a product"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}
          request {:db          @test-system/*db*
                   :body-params product}]
      (t/is (= {:status 201
                :body   product}
               (products/create-product request))))))

(t/deftest get-products-test
  (t/testing "Get all products"
    (let [product {:id             (random-uuid)
                   :name           "Test Product 1"
                   :description    "Hello, this is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (products/create-product {:db          @test-system/*db*
                                :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   [product]}
               (products/get-products {:db @test-system/*db*}))))))

(t/deftest get-product-by-id
  (t/testing "Get product by id"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (products/create-product {:db          @test-system/*db*
                                :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   product}
               (products/get-product {:db          @test-system/*db*
                                      :path-params {:id (.toString (:id product))}}))))))

(t/deftest post-create-product-test
  (t/testing "Send POST request to server to create a product"
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
      (t/is (= [:id :name :priceInCents :description :createdAt :updatedAt] (keys result)))
      (t/is (= 201 (:status response)))
      (t/is (uuid? (parse-uuid (:id result))))
      (t/is (= (:name product) (:name result)))
      (t/is (= (:description product) (:description result)))
      (t/is (= (:price-in-cents product) (:priceInCents result)))
      (t/is (= (:createdAt result) (:updatedAt result))))))

(t/deftest get-product-test
  (t/testing "GET request to server to fetch a product by ID"
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
      ;; (t/is (malli/validate spec/ProductV1 result)  (-> spec/ProductV1 (malli/explain result) (me/humanize)))
      (t/is (= [:id :name :priceInCents :description :createdAt :updatedAt] (keys result)))
      (t/is (= 200 (:status response)))
      (t/is (= (str (:id product)) (:id result)))
      (t/is (= (:name product) (:name result)))
      (t/is (= (:description product) (:description result)))
      (t/is (= (:price-in-cents product) (:priceInCents result)))
      (t/is (= (str (:created-at product)) (:createdAt result)))
      (t/is (= (str (:updated-at product)) (:updatedAt result))))))
