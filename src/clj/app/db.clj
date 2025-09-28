(ns app.db
  (:require
   [app.util.time :as time]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time :refer [read-as-instant]]
   [app.products.repository :refer [create-product]]
   [app.orders.repository :refer [create-order-with-items]]
   [app.generators :as gen]
   [clojure.tools.logging :as log]))

;; This is needed to read timestamps as instants from postgres
(read-as-instant)

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
    (let [product (gen/generate-product)
          order   (gen/generate-order)]
      (create-product db product)
      (create-order-with-items db order))))

