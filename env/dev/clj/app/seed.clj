(ns app.seed
  (:require
   [app.db]
   [app.generators :as gen]
   [app.orders.repository :refer [create-order-with-items]]
   [app.products.repository :refer [create-product]]
   [clojure.tools.logging :as log]
   [integrant.core :as ig]))

(defmethod ig/init-key :app.seed/db
  [_ {:keys [db]}]
  (when db
    (log/info "Seeding database with initial data")
    (let [product-for-order (gen/generate-product)
          order-template    (gen/generate-order)
          order             (assoc order-template :items
                                   (map #(assoc % :product-id (:id product-for-order)) (:items order-template)))]
      (create-product db product-for-order)
      (create-order-with-items db order))))
