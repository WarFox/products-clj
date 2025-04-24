(ns app.products-test
  (:import [java.time Instant])
  (:require [app.containers :as containers]
            [app.db :as db]
            [app.products :as products]
            [app.system :as system]
            [camel-snake-kebab.core :as csk]
            [clj-http.client :as http]
            [clojure.data.json :as json]
            [clojure.test :as t]
            [next.jdbc :as jdbc]))

;; db is set on startup
(def ^:dynamic *db* (atom nil))

(defn with-db
  "A fixture that sets up a PostgreSQL container for testing."
  [f]
  (let [container (containers/postgres-container system/db-spec)
        port      (-> container :mapped-ports (get 5432))
        db-spec   (assoc system/db-spec :port port)]
    (reset! *db* db-spec)
    (f)
    (containers/stop! container)))

(defn given-table
  "A fixture that sets up a PostgreSQL container and creates a table for testing."
  [f]
  (db/create-table @*db*)
  (f))

;; TODO use random port for testing
(defn with-system
  [f]
  (system/init {:server-port 3030})
  (f))

;; TODO Get database connection from system
(t/use-fixtures :once
  with-db
  given-table
  with-system)

(defn truncate-table
  [f]
  (jdbc/execute! @*db* ["truncate table products"])
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
          request {:db          @*db*
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
      (products/create-product {:db          @*db*
                                :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   [product]}
               (products/get-products {:db @*db*}))))))

(t/deftest get-product-by-id
  (t/testing "Get product by id"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}]
      (products/create-product {:db          @*db*
                                :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   product}
               (products/get-product {:db @*db*
                                      :path-params {:id (.toString (:id product))}}))))))


(t/deftest post-create-product-test
  (t/testing "Send POST request to server to create a product"
    (let [now      (str (Instant/now ))
          product  {:id             (str (random-uuid))
                    :name           "Test Product"
                    :description    "This is a test product"
                    :price-in-cents 100
                    :created-at     now
                    :updated-at     now}
          response (http/post "http://localhost:3030/v1/products"
                              {:form-params  product
                               :content-type :json})]
      (t/is (= 201 (:status response)))
      (t/is (= product (json/read-str (:body response)
                                      :key-fn csk/->kebab-case-keyword))))))
