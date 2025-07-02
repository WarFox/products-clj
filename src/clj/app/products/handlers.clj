(ns app.products.handlers
  (:require
   [app.products.domain :as domain]
   [integrant.core :as ig]))

(defn make-handlers
  "Creates handler functions with injected service dependency"
  [service]
  {:list-products
   (fn list-products [_]
     {:status 200
      :body   ((:get-products service))})

   :create-product
   (fn create-product [{:keys [body-params]}]
     {:status 201
      :body   ((:create-product service) (domain/->Product body-params))})

   :get-product
   (fn get-product [{:keys [path-params]}]
     (let [id (-> path-params :id parse-uuid)]
       {:status 200
        :body   ((:get-product service) id)}))

   :delete-product
   (fn delete-product [{:keys [path-params]}]
     (let [id (-> path-params :id parse-uuid)]
       ((:delete-product service) id)
       {:status 204
        :body   nil}))

   :update-product
   (fn update-product [{:keys [path-params body-params]}]
     (let [id (-> path-params :id parse-uuid)]
       {:status 200
        :body   ((:update-product service) id (domain/->Product body-params))}))})

(defmethod ig/init-key :app.products/handlers
  [_ {:keys [service]}]
  (make-handlers service))
