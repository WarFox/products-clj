(ns app.products.handlers
  (:require
   [app.db :as db]
   [app.domain :as domain]
   [app.products.services :as service]))

(defn list-products
  "Fetches products from the postgres"
  [{:keys [db]}]
  (let [{:keys [success failure]} (service/get-products db)]
    (if success
      {:status 200
       :body   success}
      {:status 500
       :body   failure})))

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
  (let [id                                  (-> path-params :id parse-uuid)
        {:keys [success not-found failure]} (service/get-product db id)]
    (cond
      success
      {:status 200
       :body   success}

      not-found
      {:status 404
       :body   not-found}

      failure
      {:status 500
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
