(ns app.orders.routes
  (:require
   [app.orders.handlers :as handlers]
   [app.spec :as spec]))

(def routes
  [["/orders"
    {:name ::orders
     :get  {:summary   "List orders"
            :responses {200 {:body spec/OrderV1List}}
            :handler   #'handlers/list-orders}
     :post {:summary    "Create new Order"
            :parameters {:body spec/OrderV1Request}
            :responses  {201 {:body spec/OrderV1}}
            :handler    #'handlers/create-order}}]
   ["/orders/:id"
    {:name   ::order-id
     :get    {:summary    "Get Order by uuid"
              :parameters {:path {:id uuid?}}
              :handler    #'handlers/get-order
              :responses  {200 {:body spec/OrderV1}}}
     :delete {:summary    "Delete Order by uuid"
              :parameters {:path {:id uuid?}}
              :handler    #'handlers/delete-order}}]
   ["/orders/:id/status"
    {:name ::order-status
     :put  {:summary    "Update Order status"
            :parameters {:path {:id uuid?}
                         :body {:status spec/OrderStatusEnum}}
            :handler    #'handlers/update-order-status
            :responses  {200 {:body spec/OrderV1}}}}]])
