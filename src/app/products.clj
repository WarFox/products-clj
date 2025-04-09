(ns app.products
  (:require [app.db :as db]))

(defn get-products
  "Fetches products from the postgres"
  [request]
  (let [db (-> request :reitit.core/match :data :db)
        products (db/get-products db)]
    (if (empty? products)
      {:status 404
       :body   "No products found"}
      {:status 200
       :body products})))

(defn create-product
  "Creates a new product in the postgres"
  [request]
  (let [db (:db request)
        product (db/create-product db (:body request))]
    (if product
      {:status 201
       :body   product}
      {:status 400
       :body   "Failed to create product"})))

(defn get-product
  "Fetches a product by ID from the postgres"
  [request]
  (let [db (:db request)
        id (:id request)
        product (db/get-product db id)]
    (if product
      {:status 200
       :body   product}
      {:status 404
       :body   "Product not found"})))

(def routes
  [["/products"
    {:name ::products
     :get  get-products
     :post create-product}]

   ["/products/:id"
    {:name ::product-id
     :get  get-product}]])
