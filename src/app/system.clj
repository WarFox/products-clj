(ns app.system
  (:require [app.containers :as containers]
            [app.db :as db]
            [app.handler :as handler]
            [app.server :as server]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(def db-spec
  {:dbtype   "postgres"
   :dbname   "products"
   :user     "products"
   :password "verysecret"
   :host     "localhost"
   :port     5432})

(def config
  {:adapter/jetty   {:handler (ig/ref :handler/run-app)
                     :port    3000}
   :db/instance     {:db-spec db-spec}
   :db/connection   {:db-spec db-spec
                     :db-instance (ig/ref :db/instance)}
   :db/initialize   {:db (ig/ref :db/connection)}
   :handler/run-app {:db (ig/ref :db/connection)}})

(defmethod ig/init-key :adapter/jetty
  [_ {:keys [handler port]}]
  (server/start! handler port))

(defmethod ig/init-key :db/instance
  [_ {:keys [db-spec]}]
  (println "Starting database instance")
  (containers/postgres-container db-spec))

(defmethod ig/init-key :db/connection
  [_ {:keys [db-spec db-instance]}]
  (println "Creating db connection")
  (jdbc/get-datasource (assoc db-spec
                              :port (get (:mapped-ports db-instance) (:port db-spec)))))

(defmethod ig/init-key :db/initialize
  [_ {:keys [db]}]
  (println "Initializing database" db)
  (db/create-table db)
  (db/create-product db
                     {:id             (random-uuid)
                      :name           "Sample Product"
                      :price-in-cents 1999
                      :description    "This is a sample product"}))

(defmethod ig/init-key :handler/run-app
  [_ {:keys [db]}]
  (handler/handler db))

(defmethod ig/halt-key! :adapter/jetty
  [_ server]
  (server/stop! server))

(defn init
  "Initialize the system."
  [{:keys [server-port]}]
  (ig/init (update-in
            config
            [:adapter/jetty :port]
            (fn [port]
              (if server-port
                server-port
                port)))))
