(ns api-helpers
  "Helper functions for making API requests in integration tests.

   These helpers provide a black-box testing interface that only interacts
   with the application through its public API endpoints."
  (:require
   [app.test-system :as test-system]
   [cheshire.core :as json]
   [clj-http.client :as http]
   [generators :refer [generate-product-request generate-order]]))

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

;; Product API Helpers

(defn create-product!
  "Create a product via API. Uses generated data if no product is provided."
  ([]
   (create-product! (generate-product-request)))
  ([product]
   (let [request {:body (json/encode product)}
         response (json-request :post (api-url "/products") request)]
     response)))

(defn get-products
  "Get all products via API."
  []
  (json-request :get (api-url "/products")))

(defn get-product
  "Get a product by ID via API."
  [id]
  (json-request :get (api-url (str "/products/" id))))

(defn update-product!
  "Update a product by ID via API."
  [id product]
  (let [request {:body (json/encode product)}]
    (json-request :put (api-url (str "/products/" id)) request)))

(defn delete-product!
  "Delete a product by ID via API."
  [id]
  (json-request :delete (api-url (str "/products/" id))))

;; Order API Helpers

(defn create-order!
  "Create an order via API. Uses generated data if no order is provided."
  ([]
   (create-order! (generate-order)))
  ([order]
   (let [request {:body (json/encode order)}
         response (json-request :post (api-url "/orders") request)]
     response)))

(defn get-orders
  "Get all orders via API."
  []
  (json-request :get (api-url "/orders")))

(defn get-order
  "Get an order by ID via API."
  [id]
  (json-request :get (api-url (str "/orders/" id))))

(defn update-order!
  "Update an order by ID via API."
  [id order]
  (let [request {:body (json/encode order)}]
    (json-request :put (api-url (str "/orders/" id)) request)))

(defn delete-order!
  "Delete an order by ID via API."
  [id]
  (json-request :delete (api-url (str "/orders/" id))))

;; Utility functions for common test patterns

(defn created-product
  "Create a product and return the response body (the created product)."
  ([]
   (created-product (generate-product-request)))
  ([product]
   (let [response (create-product! product)]
     (when (= 201 (:status response))
       (:body response)))))

(defn created-order
  "Create an order and return the response body (the created order)."
  ([]
   (created-order (generate-order)))
  ([order]
   (let [response (create-order! order)]
     (when (= 201 (:status response))
       (:body response)))))
