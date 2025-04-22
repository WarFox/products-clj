(ns app.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.plan :as plan]
   [next.jdbc.date-time :refer [read-as-instant]] ;; postgres needs this to deal with timestamps
   [next.jdbc.sql :as sql]))

(defn get-products
  "Fetches products from the postgres using next.jdbc"
  [db]
  (read-as-instant)
  (plan/select! db
                [:id :name :price-in-cents :description :created-at :updated-at]
                ["select * from products"]
                jdbc/unqualified-snake-kebab-opts))

(defn create-product
  "Creates a new product in the postgres using next.jdbc"
  [db product]
  (read-as-instant)
  (jdbc/execute-one! db ["INSERT INTO products (id, name, price_in_cents, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?) RETURNING *"
                         (:id product)
                         (:name product)
                         (:price-in-cents product)
                         (:description product)
                         (:created-at product)
                         (:updated-at product)]
                     jdbc/unqualified-snake-kebab-opts))

(defn get-product
  "Fetches a product by ID from the postgres using next.jdbc"
  [db id]
  (sql/get-by-id db :products id jdbc/unqualified-snake-kebab-opts))

(defn create-table
  "Creates the products table in the postgres using next.jdbc"
  [db]
  (jdbc/execute! db ["CREATE TABLE IF NOT EXISTS products (
                      id uuid PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      price_in_cents BIGINT NOT NULL,
                      description TEXT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"]))

(defn delete-product
  "Deletes a product by ID from the postgres using next.jdbc"
  [db id]
  (sql/delete! db :products {:id id}))
