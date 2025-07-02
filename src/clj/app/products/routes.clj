(ns app.products.routes
  (:require
   [app.spec :as spec]
   [integrant.core :as ig]))

(defn routes
  "Define the routes for the products API."
  [handlers]
  ["/products" {:name ::products}
   [""
    {:get  {:summary   "List products"
            :responses {200 {:body spec/ProductV1List}}
            :handler   (:list-products handlers)}
     :post {:summary    "Create new Product"
            :parameters {:body spec/ProductV1Request}
            :responses  {201 {:body spec/ProductV1}}
            :handler    (:create-product handlers)}}]
   ["/:id"
    {:name   ::product-id
     :get    {:summary    "Get Product by uuid"
              :parameters {:path {:id uuid?}}
              :handler    (:get-product handlers)
              :responses  {200 {:body spec/ProductV1}}}
     :delete {:summary    "Delete Product by uuid"
              :parameters {:path {:id uuid?}}
              :handler    (:delete-product handlers)}
     :put    {:summary    "Update Product by uuid"
              :parameters {:path {:id uuid?}
                           :body spec/ProductV1Request}
              :responses  {200 {:body spec/ProductV1}}
              :handler    (:update-product handlers)}}]])

(derive :app.products/routes :reitit.routes/api)

(defmethod ig/init-key :app.products/routes
  [_ {:keys [handlers]}]
  (routes handlers))
