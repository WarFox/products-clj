(ns app.products.handlers
  (:require
    [app.domain :as domain]
    [app.products.services :as service]))

(defn list-products
  "Fetches products from the database"
  [{:keys [db]}]
  {:status 200
   :body   (service/get-products db)})

(defn create-product
  "Creates a new product in the database"
  [{:keys [db body-params]}]
  {:status 201
   :body   (service/create-product db (domain/->Product body-params))})

(defn get-product
  "Fetches a product by ID from the database"
  [{:keys [db path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    {:status 200
     :body   (service/get-product db id)}))

(defn delete-product
  "Deletes a product by ID from the database"
  [{:keys [db path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (service/delete-product db id)
    {:status 204
     :body   nil}))
