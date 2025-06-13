(ns app.products.services
  "Service functions to work with Product domain, has business logic and validation"
  (:require
    [app.db :as db]
    [app.spec :as spec]
    [malli.core :as malli])
  (:import
   [clojure.lang ExceptionInfo]
   [java.util UUID]))

(defn create-product
  "Creates a new product in the database"
  [db product]
  (try
    (malli/assert spec/ProductV1 product)
    (db/create-product db product)
    (catch ExceptionInfo e
      (throw (ex-info "Invalid product data"
                      {:type    :system.exception/business
                       :product product
                       :cause   e})))
    (catch Exception e
      (throw (ex-info "Failed to create product"
                      {:type    :system.exception/internal
                       :product product
                       :cause   e})))))

(defn get-products
  "Fetches all products from the database"
  [db]
  (try
    (db/get-products db)
    (catch Exception e
      (throw (ex-info "Failed to get products"
                      {:type  :system.exception/internal
                       :cause e})))))

(defn get-product
  "Fetches a product by ID from the database"
  [db ^UUID id]
  (try
    (let [result (db/get-product db id)]
      (if (nil? result)
        (throw (ex-info "Product not found"
                        {:type       :system.exception/not-found
                         :product-id id}))
        result))
    (catch ExceptionInfo e
      (throw e))                                            ; Pass through our custom exceptions
    (catch Exception e
      (throw (ex-info "Failed to get product"
                      {:type       :system.exception/internal
                       :product-id id
                       :cause      e})))))
