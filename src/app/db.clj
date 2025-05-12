(ns app.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.date-time :refer [read-as-instant]]
   [next.jdbc.plan :as plan]
   [next.jdbc.sql :as sql]))

;; This is needed to read timestamps as instants from postgres
(read-as-instant)

(defn instant-now
  "Postgres compatible java.time.Instant now"
  []
  (doto (java.time.Instant/now)
    (.truncatedTo  java.time.temporal.ChronoUnit/MICROS)))

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
