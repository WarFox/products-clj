(ns app.system
  (:require [integrant.core :as ig]
            [app.containers :as containers]
            [next.jdbc :as jdbc]
            [app.db :as db]
            [ring.adapter.jetty :refer [run-jetty]]
            [app.handler :as handler]))

(def config
  {:adapter/jetty   {:handler (ig/ref :handler/run-app)
                     :port    3000}
   :db/instance     {:port 5432}
   :db/connection   {:dbtype      "postgres"
                     :dbname      "products"
                     :user        "products"
                     :password    "verysecret"
                     :host        "localhost"
                     :db-instance (ig/ref :db/instance)}
   :db/initialize   {:db (ig/ref :db/connection)}
   :handler/run-app {:db (ig/ref :db/connection)}})

(defmethod ig/init-key :adapter/jetty
  [_ {:keys [handler] :as opts}]
  (run-jetty handler (-> opts (dissoc :handler) (assoc :join? false))))

(defmethod ig/init-key :db/instance
  [_ {:keys [port]}]
  (containers/postgres-container port))

(defmethod ig/init-key :db/connection
  [_ {:keys [db-instance] :as opts}]
  (jdbc/get-datasource (assoc opts
                              :port (get (:mapped-ports db-instance) 5432))))

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
  (.stop server))

(defn init
  "Initialize the system."
  []
  (ig/init config))
