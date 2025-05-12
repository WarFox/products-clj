(ns app.services.product
  "Service functions to work with Product domain, has business logic
   Validate domain model
   Return nil in case of errors
   Log errors"
  (:require
   [app.db :as db]))

(defn create-product
  [db product]
  (try
    {:success (db/create-product db product)}
    (catch Exception e
      {:failure
       {:message "Failed to create product"
        :product product
        :reason (.getMessage e)}})))

(defn get-product
  [db ^java.util.UUID id]
  (try
    {:success (db/get-product db id)}
    (catch Exception e
      {:failure
       {:message "Failed to get product"
        :product-id id
        :reason (.getMessage e)}})))
