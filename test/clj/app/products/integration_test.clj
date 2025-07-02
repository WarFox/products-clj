(ns app.products.integration-test
  (:require
   [app.products.repository :as repository]
   [app.spec :as spec]
   [app.test-system :as test-system]
   [app.util.time :as time]
   [cheshire.core :as json]
   [clj-http.client :as http]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-system]]
   [malli.core :as malli]
   [malli.error :as me]))

(use-fixtures :once
  with-system)

(use-fixtures :each
  truncate-table)

(defn api-url
  "Build API URL for the given path."
  [path]
  (str (test-system/base-url) "/v1" path))

(defn json-request
  "Make a JSON request with default options."
  [method url & [opts]]
  (let [default-opts {:content-type :json
                      :accept :json
                      :as :json
                      :throw-exceptions false}]
    (http/request (merge default-opts
                         {:method method
                          :url url}
                         opts))))

(deftest create-product-integration-test
  (testing "Create a product via API"
    (let [product {:name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100}
          request {:body (json/encode product)}]
      (is (malli/validate spec/ProductV1Request product)
          (-> spec/ProductV1Request (malli/explain product) (me/humanize)))
      (let [response (json-request :post (api-url "/products") request)
            {:keys [status body]} response]
        (is (= 201 status))
        (is (string? (:id body)))
        (is (string? (:createdAt body)))
        (is (string? (:updatedAt body)))
        (is (= (:name body) "Test Product"))
        (is (= (:description body) "This is a test product"))
        (is (= (:priceInCents body) 100))
        (is (= (:createdAt body) (:updatedAt body)))))))

(deftest get-products-integration-test
  (testing "Get all products via API"
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
      ;; Create test products
      (doseq [product products]
        (repository/create-product (test-system/db) product))
      
      (let [response (json-request :get (api-url "/products"))
            {:keys [status body]} response]
        (is (= 200 status))
        (is (= 2 (count body)))
        ;; Verify the products are returned (order might differ)
        (let [returned-names (set (map :name body))]
          (is (contains? returned-names "Test Product 1"))
          (is (contains? returned-names "Test Product 2")))))))

(deftest get-product-by-id-integration-test
  (testing "Get product by id via API"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      ;; Create test product
      (repository/create-product (test-system/db) product)
      
      (let [response (json-request :get (api-url (str "/products/" (:id product))))
            {:keys [status body]} response]
        (is (= 200 status))
        (is (= (:id body) (str (:id product))))
        (is (= (:name body) "Test Product"))
        (is (= (:description body) "This is a test product"))
        (is (= (:priceInCents body) 100))))))

(deftest get-product-by-id-not-found-integration-test
  (testing "Get product by non-existent id returns 404"
    (let [non-existent-id (random-uuid)
          response (json-request :get (api-url (str "/products/" non-existent-id)))
          {:keys [status]} response]
      (is (= 404 status)))))
