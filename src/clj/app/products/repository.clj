(ns app.products.repository
  (:require
    [next.jdbc :as jdbc]
    [next.jdbc.plan :as plan]
    [next.jdbc.sql :as sql]
    [clojure.tools.logging :as log]
    [integrant.core :as ig]))

(defn get-products
  "Fetches products from the postgres using next.jdbc"
  [db]
  (plan/select! db
                [:id :name :price-in-cents :description :created-at :updated-at]
                ["select * from products"]
                jdbc/unqualified-snake-kebab-opts))

(defn create-product
  "Creates a new product in the postgres using next.jdbc"
  [db product]
  (log/info "Repository: Creating product:" product)
  (sql/insert! db
               :products product
               (assoc jdbc/unqualified-snake-kebab-opts
                 :suffix "RETURNING *")))

(defn get-product
  "Fetches a product by ID from the postgres using next.jdbc"
  [db id]
  (sql/get-by-id db :products id jdbc/unqualified-snake-kebab-opts))

(defn delete-product
  "Deletes a product by ID from the postgres using next.jdbc"
  [db id]
  (sql/delete! db :products {:id id}))

(defn update-product
  "Updates a product by ID from the postgres using next.jdbc"
  [db id product]
  (sql/update! db
               :products product
               {:id id}
               (assoc jdbc/unqualified-snake-kebab-opts
                 :suffix "RETURNING *")))

(defmethod ig/init-key :app.products/repository
  [_ {:keys [db]}]
  {:get-products    (partial get-products db)
   :create-product  (partial create-product db)
   :get-product     (partial get-product db)
   :delete-product  (partial delete-product db)
   :update-product  (partial update-product db)})
