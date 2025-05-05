(ns app.products-test
  (:import [java.time Instant])
  (:require [app.db :as db]
            [app.products :as products]
            [app.system :as system]
            [app.test-system :as test-system]
            [app.server :as server]
            [camel-snake-kebab.core :as csk]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [clojure.test :as t]
            [next.jdbc :as jdbc]))

;; TODO use migrations for setting up tables
(defn given-table
  [f]
  (db/create-table @test-system/*db*)
  (f))

;; TODO use random port for testing
(defn with-system
  [f]
  (let [sys (test-system/init-test-system)]
    (f)
    (system/halt! sys)))

(t/use-fixtures :once
  with-system
  given-table)

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
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}
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
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}]
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
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}]
      (products/create-product {:db          @test-system/*db*
                                :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   product}
               (products/get-product {:db          @test-system/*db*
                                      :path-params {:id (.toString (:id product))}}))))))

(defn millis
  [t]
  (cond
    (inst? t)
    (.truncatedTo t java.time.temporal.ChronoUnit/MILLIS)

    (string? t) ;; parse string to Instant and truncate to millis
    (try
      (let [instant (Instant/parse t)]
        (.truncatedTo instant java.time.temporal.ChronoUnit/MILLIS))
      (catch Exception e
        t))
    :else t))

(t/deftest post-create-product-test
  (t/testing "Send POST request to server to create a product"
    (let [nowstr   (str (Instant/now))
          product  {:id             (str (random-uuid))
                    :name           "Test Product"
                    :description    "This is a test product"
                    :price-in-cents 100
                    :created-at     nowstr
                    :updated-at     nowstr}
          url      (format "http://localhost:%s/v1/products" (server/get-port @test-system/*server*))
          response (http/post url
                              {:form-params  product
                               :content-type :json})
          result   (json/read-str (:body response)
                                  :key-fn csk/->kebab-case-keyword)]
      (t/is (= 201 (:status response)))
      (t/is (= (:id product) (:id result)))
      (t/is (= (:name product) (:name result)))
      (t/is (= (:description product) (:description result)))
      (t/is (= (:price-in-cents product) (:price-in-cents result)))
      (t/is (= (millis (:created-at product)) (millis (:created-at result))))
      (t/is (= (millis (:updated-at product)) (millis (:updated-at result)))))))
