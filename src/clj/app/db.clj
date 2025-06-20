(ns app.db
  (:require
   [app.util.time :as time]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time :refer [read-as-instant]]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]
   [clojure.tools.logging :as log]))

;; This is needed to read timestamps as instants from postgres
(read-as-instant)

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

(defmethod ig/init-key :app.db/spec
  [_ opts]
  opts)

(defmethod ig/init-key :app.db/connection
  [_ {:keys [db-spec container]}]
  (log/info "Initializing database connection")
  (let [mapped-ports (:mapped-ports container {})
        effective-port (get mapped-ports (:port db-spec) (:port db-spec))
        effective-db-spec (assoc db-spec :port effective-port)]

    (log/debug "Database connection parameters:" (dissoc effective-db-spec :password))
    (jdbc/get-datasource effective-db-spec)))

(defmethod ig/init-key :app.db/seed
  [_ {:keys [db]}]
  (when db
    (log/info "Seeding database with initial data")
    (create-product db
                    {:id             (random-uuid)
                     :name           "Sample Product"
                     :price-in-cents 1999
                     :description    "This is a sample product"
                     :created-at     (time/instant-now :micros)
                     :updated-at     (time/instant-now :micros)})))
