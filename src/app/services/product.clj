(ns app.services.product
  "Service functions to work with Product domain, has business logic
   Validate domain model
  {:success {}
   :failure {}}
   Log errors"
  (:require
   [app.db :as db]
   [app.spec :as spec]
   [malli.core :as malli]))

(defn create-product
  [db product]
  (try
    (malli/assert spec/ProductV1 product)
    {:success (db/create-product db product)}
    (catch Exception e
      {:failure
       {:message "Failed to create product"
        :product product
        :reason e}})))

(defn get-products
  [db]
  (try
    {:success (db/get-products db)}
    (catch Exception e
      {:failure
       {:message "Failed to get products"
        :reason (.getMessage e)}})))

(defn get-product
  [db ^java.util.UUID id]
  (try
    (let [result (db/get-product db id)]
      (if (nil? result)
        {:not-found
         {:message "Product not found"
          :product-id id}}
        {:success result}))
    (catch Exception e
      {:failure
       {:message "Failed to get product"
        :product-id id
        :reason (.getMessage e)}})))
