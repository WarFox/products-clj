(ns app.orders.routes
  (:require
   [app.spec :as spec]
   [integrant.core :as ig]))

(defn routes
  [handlers]
  ["/orders" {:name ::orders}
   [""
    {:get  {:summary   "List orders"
            :responses {200 {:body spec/OrderV1List}}
            :handler   (:list-orders handlers)}
     :post {:summary    "Create new Order"
            :parameters {:body spec/OrderV1Request}
            :responses  {201 {:body spec/OrderV1}}
            :handler    (:create-order handlers)}}]
   ["/:id"
    {:name   ::order-id
     :get    {:summary    "Get Order by uuid"
              :parameters {:path {:id uuid?}}
              :handler    (:get-order handlers)
              :responses  {200 {:body spec/OrderV1}}}
     :delete {:summary    "Delete Order by uuid"
              :parameters {:path {:id uuid?}}
              :handler    (:delete-order handlers)}}]
   ["/:id/status"
    {:name ::order-status
     :put  {:summary    "Update Order status"
            :parameters {:path {:id uuid?}
                         :body {:status spec/OrderStatusEnum}}
            :handler    (:update-order-status handlers)
            :responses  {200 {:body spec/OrderV1}}}}]])

(derive :app.orders/routes :reitit.routes/api)

(defmethod ig/init-key :app.orders/routes
  [_ {:keys [handlers]}]
  (routes handlers))
