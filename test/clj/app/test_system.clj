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

(defn db
  "Get the test database connection."
  []
  (or @*db* (throw (ex-info "Test database not initialized" {}))))

(defn server
  "Get the test server instance."
  []
  (or @*server* (throw (ex-info "Test server not initialized" {}))))

(defn log-ds
  "Get the test database connection with logging enabled."
  []
  (or (when @*db*
        (jdbc/with-logging @*db* (fn [sym [sql & params]]
                                   (log/info sym "Executing SQL:" sql "with params:" params))))
      (throw (ex-info "Test database with logging not initialized" {}))))

;; Override the init-key methods for the test system
(defmethod ig/init-key :app.server/jetty
  [_ {:keys [handler port]}]
  (reset! *server* (server/start! handler port)))

(defmethod ig/init-key :app.db/connection
  [_ {:keys [db-spec container]}]
  (log/info "Setting up test database connection")
  (let [mapped-ports      (:mapped-ports container {})
        effective-db-spec (update db-spec :port #(get mapped-ports % %))]
    (reset! *db* (jdbc/get-datasource effective-db-spec))))

(defn init-test-system
  "Initialize the system."
  []
  (log/info "Initializing test system")
  (system/init (config/system-config {:profile :test})))

(defn init-db
  "Initialize the database connection for testing without starting the server."
  []
  (log/info "Initializing test database connection")
  (system/init
   (config/system-config {:profile :test})
   [:app.system/env :app.db/spec :app.db/connection :app.migrations/flyway]))
