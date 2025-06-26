(ns app.products.services
  "Service functions to work with Product domain, has business logic and validation"
  (:require
   [app.products.repository :as repository]
   [app.spec :as spec]
   [malli.core :as malli]
   [clojure.tools.logging :as log])
  (:import (java.util UUID)))

(defn create-product
  "Creates a new product in the database"
  [db product]
  (log/info "Creating product:" product)
  (malli/assert spec/ProductV1 product)
  (repository/create-product db product))

(defn get-products
  "Fetches all products from the database"
  [db]
  (repository/get-products db))

(defn get-product
  "Fetches a product by ID from the database"
  [db ^UUID id]
  (or (repository/get-product db id)
      (throw (ex-info "Product not found"
                      {:type       :system.exception/not-found
                       :product-id id}))))

(defn delete-product
  "Deletes a product by ID from the database"
  [db ^UUID id]
  (let [result (repository/delete-product db id)]
    (when (zero? result)
      (throw (ex-info "Product not found"
                      {:type       :system.exception/not-found
                       :product-id id})))))

(defn update-product
  "Updates a product by ID from the database"
  [db ^UUID id product]
  (log/info "Updating product:" product)
  (malli/assert spec/ProductV1 product)
  (repository/update-product db id product))
