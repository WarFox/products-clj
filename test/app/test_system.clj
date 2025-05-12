(ns app.test-system
  (:require
   [app.config :as config]
   [app.server :as server]
   [app.system] ;; make sure app system is loaded
   [integrant.core :as ig]
   [next.jdbc :as jdbc]))

;; Test System
(def ^:dynamic *server* (atom nil))

(def ^:dynamic *db* (atom nil))

(defmethod ig/init-key :adapter/jetty
  [_ {:keys [handler port]}]
  (reset! *server* (server/start! handler port)))

(defmethod ig/init-key :db/connection
  [_ {:keys [db-spec test-container]}]
  (let [mapped-ports (:mapped-ports test-container)
        db-spec      (update db-spec :port #(get mapped-ports % %))]
    (reset! *db* (jdbc/get-datasource db-spec))))

(defn init-test-system
  "Initialize the system."
  []
  (let [config (assoc-in (config/config {:profile :dev})
                         [:adapter/jetty :port] 0)]
    (ig/init config)))

(defn init-db
  []
  (ig/init (config/config {:profile :dev})
           [:test/container :db/spec :db/connection :db/initialize]))
