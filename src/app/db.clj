(ns app.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.plan :as plan]))

(defn get-products
  "Fetches products from the postgres using next.jdbc"
  [db]
  (plan/select! db [:id :name :price :description] ["SELECT * FROM products"]))

(defn create-product
  "Creates a new product in the postgres using next.jdbc"
  [db product]
  (jdbc/execute! db ["INSERT INTO products (id, name, price_in_cents, description) VALUES (?, ?, ?, ?) RETURNING *"
                     (:id product)
                     (:name product)
                     (:price-in-cents product)
                     (:description product)]
                 jdbc/unqualified-snake-kebab-opts))

(defn get-product
  "Fetches a product by ID from the postgres using next.jdbc"
  [db id]
  (plan/select-one! db [:id :name :price :description] ["SELECT * FROM products WHERE id = ?" (parse-uuid id)]))

(defn create-table
  "Creates the products table in the postgres using next.jdbc"
  [db]
  (jdbc/execute! db ["CREATE TABLE IF NOT EXISTS products (
                      id uuid PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      price_in_cents BIGINT NOT NULL,
                      description TEXT
                      )"]))

(defn delete-product
  "Deletes a product by ID from the postgres using next.jdbc"
  [db id]
  (jdbc/execute! db ["DELETE FROM products WHERE id = ?" (parse-uuid id)]))
