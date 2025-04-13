(ns app.products
  (:require [app.db :as db]))

(defn get-products
  "Fetches products from the postgres"
  [request]
  (let [db (:db request)
        products (db/get-products db)]
    (if (empty? products)
      {:status 404
       :body   "No products found"}
      {:status 200
       :body products})))

(defn create-product
  "Creates a new product in the postgres"
  [request]
  (let [db      (:db request)
        body    (:body request)
        product (-> body
                     (assoc
                       :id (or (:id body)
                               (random-uuid))
                       :price-in-cents (:priceInCents body))
                     (dissoc :priceInCents))]
    (if-let [product (db/create-product db product)]
      {:status 201
       :body   product}
      {:status 400
       :body   "Failed to create product"})))

(defn get-product
  "Fetches a product by ID from the postgres"
  [request]
  (let [db (:db request)
        id (:id (:path-params request))
        product (db/get-product db id)]
    (if product
      {:status 200
       :body   product}
      {:status 404
       :body   "Product not found"})))

(defn delete-product
  "Deletes a product by ID from the postgres"
  [request]
  (let [db (:db request)
        id (:id (:path-params request))
        deleted (db/delete-product db id)]
    (if deleted
      {:status 204}
      {:status 404
       :body   "Product not found"})))

(def routes
  [["/products"
    {:name ::products
     :get  get-products
     :post create-product}]

   ["/products/:id"
    {:name   ::product-id
     :get    get-product
     :delete delete-product}]])
