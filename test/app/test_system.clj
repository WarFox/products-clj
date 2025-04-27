(ns app.test-system
  (:require [app.config :as config]
            [app.test-containers :as tc]
            [app.server :as server]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]))

;; Test System
(def ^:dynamic *server-port* (atom nil))

(def ^:dynamic *db* (atom nil))

(defmethod ig/init-key :db/container
  [_ {:keys [db-spec]}]
  (tc/postgres-container db-spec))

(defmethod ig/halt-key! :db/container
  [_ container]
  (tc/stop! container))

(defmethod ig/init-key :db/connection
  [_ {:keys [mapped-port]}]
  (let [config (config/config :test)
        db-spec (config/db-spec config)]
    (reset! *db* (jdbc/get-datasource
                  (assoc db-spec :port mapped-port)))))

(defmethod ig/init-key :db/mapped-port
  [_ {:keys [container]}]
  (let [db-port (-> container :mapped-ports (get 5432))]
    db-port))

(defmethod ig/init-key :server/port
  [_ server]
  (let [port (server/server-port server)]
    (println "Server listening on port" port)
    (reset! *server-port*  port)))

(defn init-test-system
  "Initialize the system."
  []
  (let [config (assoc-in (config/config :test)
                         [:adapter/jetty :port] 0)]
    (ig/init config)))
