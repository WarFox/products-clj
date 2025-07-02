(ns app.products.handlers
  (:require
   [app.products.domain :as domain]
   [clojure.tools.logging :as log]
   [integrant.core :as ig]))

(defn- list-products
  "Handler for listing all products."
  [service _request]
  (log/info "Listing all products")
  {:status 200
   :body   ((:get-products service))})

(defn- create-product
  "Handler for creating a new product."
  [service {:keys [body-params]}]
  (log/info "Creating product with params:" body-params)
  (let [product (domain/->Product body-params)]
    {:status 201
     :body   ((:create-product service) product)}))

(defn- get-product
  "Handler for getting a product by ID."
  [service {:keys [path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (log/info "Getting product with ID:" id)
    {:status 200
     :body   ((:get-product service) id)}))

(defn- delete-product
  "Handler for deleting a product by ID."
  [service {:keys [path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (log/info "Deleting product with ID:" id)
    ((:delete-product service) id)
    {:status 204
     :body   nil}))

(defn- update-product
  "Handler for updating a product by ID."
  [service {:keys [path-params body-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (log/info "Updating product with ID:" id "params:" body-params)
    {:status 200
     :body   ((:update-product service) id (domain/->Product body-params))}))

(defn make-handlers
  "Creates handler functions with injected service dependency."
  [service]
  {:list-products  (partial list-products service)
   :create-product (partial create-product service)
   :get-product    (partial get-product service)
   :delete-product (partial delete-product service)
   :update-product (partial update-product service)})

(defmethod ig/init-key :app.products/handlers
  [_ {:keys [service]}]
  (make-handlers service))
