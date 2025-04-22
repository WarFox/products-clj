(ns app.products
  (:import [java.time Instant])
  (:require [app.db :as db]))

(defn get-products
  "Fetches products from the postgres"
  [request]
  (let [db       (:db request)
        products (db/get-products db)]
    (if (empty? products)
      {:status 404
       :body   "No products found"}
      {:status 200
       :body   products})))

(defn create-product
  "Creates a new product in the postgres"
  [request]
  (let [db      (:db request)
        product (:body-params request)
        product (assoc product
                       :id             (or (:id product) (random-uuid))
                       :created-at     (or (:created-at product) (Instant/now))
                       :updated-at     (or (:updated-at product) (Instant/now)))]
    (if-let [product (db/create-product db product)]
      {:status 201
       :body   product}
      {:status 400
       :body   "Failed to create product"})))

(defn get-product
  "Fetches a product by ID from the postgres"
  [request]
  (let [db      (:db request)
        id      (-> request :path-params :id parse-uuid)
        product (db/get-product db id)]
    (if product
      {:status 200
       :body   product}
      {:status 404
       :body   "Product not found"})))

(defn delete-product
  "Deletes a product by ID from the postgres"
  [request]
  (let [db      (:db request)
        id      (-> request :path-params :id parse-uuid)
        deleted (db/delete-product db id)]
    (if deleted
      {:status 204}
      {:status 404
       :body   "Product not found"})))

(def routes
  [["/products"
    {:name ::products
     :get  {:summary   "List products"
            :responses {200 {:body spec/ProductV1List}}
            :handler   get-products}
     :post {:summary    "Create new Product"
            :parameters {:body spec/ProductV1Request}
            :responses  {201 {:body spec/ProductV1}}
            :handler    create-product}}]
   ["/products/:id"
    {:name   ::product-id
     :get    {:summary    "Get Product by uuid"
              :parameters {:path {:id uuid?}}
              :handler    get-product
              :responses  {200 {:body spec/ProductV1}}}
     :delete {:parameters {:path {:id uuid?}}
              :handler    delete-product}}]])
