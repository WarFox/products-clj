(ns app.orders.integration-test
  (:require
   [app.products.repository :as product-repository]
   [app.spec :as spec]
   [app.test-system :as test-system]
   [cheshire.core :as json]
   [clj-http.client :as http]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-system]]
   [generators :refer [generate-product]]
   [malli.core :as malli]
   [malli.error :as me]
   [malli.generator :as mg]))

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

(deftest create-order-integration-test
  (testing "Create an order via API"
    (let [order-items           (repeatedly 2 #(mg/generate spec/OrderItemV1Request))
          order-request         (assoc (mg/generate spec/OrderV1Request)
                                       :items order-items)
          product-ids           (mapv :product-id order-items)
          _                     (doseq [product-id product-ids]
                                  (product-repository/create-product (test-system/db) (generate-product product-id)))
          total-amount          (reduce + (map #(* (:quantity %) (:price-per-unit %)) order-items))
          response              (json-request :post (api-url "/orders")
                                              {:body (json/encode order-request)})
          {:keys [status body]} response]
      (is (= 201 status))
      (is (string? (:id body)))
      (is (string? (:createdAt body)))
      (is (string? (:updatedAt body)))
      (is (= (:status body) "pending"))
      (is (= (:totalAmount body) total-amount))
      (is (= (count (:items body)) (count order-items)))
      (is (= (:createdAt body) (:updatedAt body))))))

(deftest get-orders-integration-test
  (testing "Get all orders via API"
    ;; This test would require setting up orders and items in the database
    ;; For now, let's test the empty case
    (let [response (json-request :get (api-url "/orders"))
          {:keys [status body]} response]
      (is (= 200 status))
      (is (vector? body))
      (is (= 0 (count body))))))

(deftest get-order-by-id-not-found-integration-test
  (testing "Get order by non-existent id returns 404"
    (let [non-existent-id (random-uuid)
          response (json-request :get (api-url (str "/orders/" non-existent-id)))
          {:keys [status]} response]
      (is (= 404 status)))))
