(ns app.orders.handlers
  (:require
   [app.orders.domain :as domain]
   [integrant.core :as ig]))

(defn make-handlers
  "Creates handler functions with injected service dependency"
  [service]
  {:list-orders
   (fn list-orders [_]
     {:status 200
      :body   ((:get-orders service))})

   :create-order
   (fn create-order [{:keys [body-params]}]
     (let [order (domain/->Order body-params)]
       {:status 201
        :body   ((:create-order-with-items service) order)}))

   :get-order
   (fn get-order [{:keys [path-params]}]
     (let [id (-> path-params :id parse-uuid)]
       {:status 200
        :body   ((:get-order service) id)}))

   :delete-order
   (fn delete-order [{:keys [path-params]}]
     (let [id (-> path-params :id parse-uuid)]
       ((:delete-order service) id)
       {:status 204
        :body   nil}))

   :update-order-status
   (fn update-order-status [{:keys [path-params body-params]}]
     (let [id (-> path-params :id parse-uuid)
           status (:status body-params)]
       {:status 200
        :body   ((:update-order-status service) id status)}))})

(defmethod ig/init-key :app.orders/handlers
  [_ {:keys [service]}]
  (make-handlers service))
