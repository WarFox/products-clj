(ns app.products.services
  "Service functions to work with Product domain, has business logic and validation"
  (:require
   [app.spec :as spec]
   [malli.core :as malli]
   [clojure.tools.logging :as log]
   [integrant.core :as ig])
  (:import (java.util UUID)))

(defn create-product
  "Creates a new product in the database"
  [repository product]
  (log/info "Creating product:" product)
  (malli/assert spec/ProductV1 product)
  ((:create-product repository) product))

(defn get-products
  "Fetches all products from the database"
  [repository]
  ((:get-products repository)))

(defn get-product
  "Fetches a product by ID from the database"
  [repository ^UUID id]
  (or ((:get-product repository) id)
      (throw (ex-info "Product not found"
                      {:type       :system.exception/not-found
                       :product-id id}))))

(defn delete-product
  "Deletes a product by ID from the database"
  [repository ^UUID id]
  (let [result ((:delete-product repository) id)]
    (if (pos? (:next.jdbc/update-count result))
      (:next.jdbc/update-count result)
      (throw (ex-info "Product not found"
                      {:type       :system.exception/not-found
                       :product-id id})))))

(defn update-product
  "Updates a product by ID from the database"
  [repository ^UUID id product]
  (log/info "Updating product:" product)
  (malli/assert spec/ProductV1 product)
  ((:update-product repository) id product))

(defmethod ig/init-key :app.products/service
  [_ {:keys [repository]}]
  {:create-product  (partial create-product repository)
   :get-products    (partial get-products repository)
   :get-product     (partial get-product repository)
   :delete-product  (partial delete-product repository)
   :update-product  (partial update-product repository)})
