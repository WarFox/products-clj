(ns app.products
  "Controller for Products domain, works with request objects,
  Converts requests to domain objects"
  (:require [app.db :as db]
            [app.domain :as domain]
            [app.services.product :as service]
            [app.spec :as spec]))

(defn get-products
  "Fetches products from the postgres"
  [{:keys [db]}]
  (let [products (db/get-products db)]
    (if (empty? products)
      {:status 404
       :body   "No products found"}
      {:status 200
       :body   products})))

(defn create-product
  "Creates a new product in the postgres"
  [{:keys [db body-params]}]
  (let [{:keys [success failure]} (service/create-product
                                   db
                                   (domain/->Product body-params))]
    (if success
      {:status 201
       :body   success}
      {:status 500
       :body   failure})))

(defn get-product
  "Fetches a product by ID from the postgres"
  [{:keys [db path-params]}]
  (let [id                        (-> path-params :id parse-uuid)
        {:keys [success failure]} (service/get-product db id)]
    (if success
      {:status 200
       :body   success}
      {:status 404
       :body   failure})))

(defn delete-product
  "Deletes a product by ID from the postgres"
  [{:keys [db path-params]}]
  (let [id      (-> path-params :id parse-uuid)
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
