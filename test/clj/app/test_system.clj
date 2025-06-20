(ns app.test-system
  (:require
   [app.config :as config]
   [app.server :as server]
   [app.system :as system]
   [integrant.core :as ig]
   [next.jdbc :as jdbc]
   [clojure.tools.logging :as log]))

;; Test System
(def ^:dynamic *server* (atom nil))

(def ^:dynamic *db* (atom nil))

;; Override the init-key methods for the test system
(defmethod ig/init-key :app.server/jetty
  [_ {:keys [handler port]}]
  (reset! *server* (server/start! handler port)))

(defmethod ig/init-key :app.db/connection
  [_ {:keys [db-spec container]}]
  (log/info "Setting up test database connection")
  (let [mapped-ports (:mapped-ports container {})
        effective-db-spec (update db-spec :port #(get mapped-ports % %))]
    (reset! *db* (jdbc/get-datasource effective-db-spec))))

(defn init-test-system
  "Initialize the system."
  []
  (let [config (config/system-config {:profile :test})]
    (system/init config)))

(defn init-db
  "Initialize the database connection for testing without starting the server."
  []
  (log/info "Initializing test database connection")
  (let [config (config/system-config {:profile :test})]
    (system/init
     config
     [:app.system/env :app.db/spec :app.db/connection :app.migrations/flyway])))
