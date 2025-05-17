(ns app.system
  (:require
   [app.db :as db]
   [app.handler :as handler]
   [app.test-containers :as tc]
   [app.server :as server]
   [app.util.time :as time]
   [app.migrations :as migrations]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]))

(defmethod ig/init-key :app.system/env
  [_ env]
  env)

(defmethod ig/init-key :app.db/spec
  [_ opts]
  opts)

(defmethod ig/init-key :app.test/container
  [_ {:keys [db-spec]}]
  (tc/postgres-container db-spec))

(defmethod ig/halt-key! :app.test/container
  [_ container]
  (tc/stop! container))

(defmethod ig/init-key :app.db/connection
  [_ {:keys [db-spec test-container]}]
  (let [mapped-ports (:mapped-ports test-container)
        db-spec      (update db-spec :port #(get mapped-ports % %))]
    (jdbc/get-datasource db-spec)))

;; TODO move to migrations
(defmethod ig/init-key :app.db/initialize
  [_ {:keys [db]}]
  (println "Initializing database" db)
  (migrations/migrate db)
  (db/create-product db
                     {:id             (random-uuid)
                      :name           "Sample Product"
                      :price-in-cents 1999
                      :description    "This is a sample product"
                      :created-at     (time/instant-now :micros)
                      :updated-at     (time/instant-now :micros)}))

(defmethod ig/init-key :app.handler/api
  [_ {:keys [db]}]
  (handler/handler db))

(defmethod ig/init-key :app.server/jetty
  [_ {:keys [handler port]}]
  (server/start! handler port))

(defmethod ig/halt-key! :app.server/jetty
  [_ server]
  (server/stop! server))

(defn init
  "Initialize the system."
  ([config]
   (-> config
       (ig/expand)
       (ig/init)))
  ([config opts]
   (-> config
       (ig/expand)
       (ig/init opts))))

(defn halt!
  "Halt the system."
  [system]
  (ig/halt! system))
