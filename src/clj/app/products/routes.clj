(ns app.products.routes
  (:require
   [app.products.handlers :as handlers]
   [app.spec :as spec]))

(def routes
  [["/products"
    {:name ::products
     :get  {:summary   "List products"
            :responses {200 {:body spec/ProductV1List}}
            :handler   #'handlers/list-products}
     :post {:summary    "Create new Product"
            :parameters {:body spec/ProductV1Request}
            :responses  {201 {:body spec/ProductV1}}
            :handler    #'handlers/create-product}}]
   ["/products/:id"
    {:name   ::product-id
     :get    {:summary    "Get Product by uuid"
              :parameters {:path {:id uuid?}}
              :handler    #'handlers/get-product
              :responses  {200 {:body spec/ProductV1}}}
     :delete {:summary    "Delete Product by uuid"
              :parameters {:path {:id uuid?}}
              :handler    #'handlers/delete-product}
     :put    {:summary    "Update Product by uuid"
              :parameters {:path {:id uuid?}
                           :body spec/ProductV1Request}
              :responses  {200 {:body spec/ProductV1}}
              :handler    #'handlers/update-product}}]])
