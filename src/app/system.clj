(ns app.system
  (:require [app.db :as db]
            [app.handler :as handler]
            [app.test-containers :as tc]
            [app.server :as server]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key :system/env
  [_ env]
  env)

(defmethod ig/init-key :test/container
  [_ {:keys [db-spec]}]
  (tc/postgres-container db-spec))

(defmethod ig/halt-key! :test/container
  [_ container]
  (tc/stop! container))

(defmethod ig/init-key :db/spec
  [_ opts]
  opts)

(defmethod ig/init-key :db/connection
  [_ {:keys [db-spec test-container]}]
  (let [mapped-ports (:mapped-ports test-container)
        db-spec      (update db-spec :port #(get mapped-ports % %))]
    (jdbc/get-datasource db-spec)))

;; TODO move to migrations
(defmethod ig/init-key :db/initialize
  [_ {:keys [db]}]
  (println "Initializing database" db)
  (db/create-table db)
  (db/create-product db
                     {:id             (random-uuid)
                      :name           "Sample Product"
                      :price-in-cents 1999
                      :description    "This is a sample product"
                      :created-at     (java.time.Instant/now)
                      :updated-at     (java.time.Instant/now)}))

(defmethod ig/init-key :handler/run-app
  [_ {:keys [db]}]
  (handler/handler db))

(defmethod ig/init-key :adapter/jetty
  [_ {:keys [handler port]}]
  (server/start! handler port))

(defmethod ig/halt-key! :adapter/jetty
  [_ server]
  (server/stop! server))

(defn init
  "Initialize the system."
  [config]
  (-> config
      (ig/expand)
      (ig/init)))

(defn halt!
  "Halt the system."
  [system]
  (ig/halt! system))
