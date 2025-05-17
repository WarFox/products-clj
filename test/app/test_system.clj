(ns app.test-system
  (:require
   [app.config :as config]
   [app.server :as server]
   [app.system :as system]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]))

;; Test System
(def ^:dynamic *server* (atom nil))

(def ^:dynamic *db* (atom nil))

;; Override the init-key methods for the test system
(defmethod ig/init-key :app.server/jetty
  [_ {:keys [handler port]}]
  (reset! *server* (server/start! handler port)))

(defmethod ig/init-key :app.db/connection
  [_ {:keys [db-spec test-container]}]
  (let [mapped-ports (:mapped-ports test-container)
        db-spec      (update db-spec :port #(get mapped-ports % %))]
    (reset! *db* (jdbc/get-datasource db-spec))))

(defn init-test-system
  "Initialize the system."
  []
  (let [config (config/system-config {:profile :test})]
    (system/init config)))

(defn init-db
  []
  (let [config (config/system-config {:profile :test})]
    (system/init
     config
     [:app.test/container :app.db/spec :app.db/connection :app.db/initialize])))
