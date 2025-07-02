(ns app.orders.handlers
  (:require
   [app.orders.domain :as domain]
   [clojure.tools.logging :as log]
   [integrant.core :as ig]))

(defn- list-orders
  [service _request]
  (log/info "Listing all orders")
  {:status 200
   :body   ((:get-orders service))})

(defn- create-order
  [service {:keys [body-params]}]
  (log/info "Creating order with params:" body-params)
  (let [order (domain/->Order body-params)]
    {:status 201
     :body   ((:create-order-with-items service) order)}))

(defn- get-order
  [service {:keys [path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (log/info "Getting order with ID:" id)
    {:status 200
     :body   ((:get-order service) id)}))

(defn- delete-order
  [service {:keys [path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    ((:delete-order service) id)
    {:status 204
     :body   nil}))

(defn- update-order-status
  [service {:keys [path-params body-params]}]
  (let [id     (-> path-params :id parse-uuid)
        status (:status body-params)]
    {:status 200
     :body   ((:update-order-status service) id status)}))

(defn make-handlers
  "Creates handler functions with injected service dependency"
  [service]
  {:list-orders (partial list-orders service)
   :create-order (partial create-order service)
   :get-order (partial get-order service)
   :delete-order (partial delete-order service)
   :update-order-status (partial update-order-status service)})

(defmethod ig/init-key :app.orders/handlers
  [_ {:keys [service]}]
  (make-handlers service))
