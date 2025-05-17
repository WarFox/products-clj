(ns app.test-containers
  (:require
   [integrant.core :as ig]
   [clj-test-containers.core :as tc]))

(defn postgres-container
  [{:keys [dbname user password port]}]
  (println "Starting PostgreSQL container")
  (-> (tc/create {:image-name    "postgres:14.1"
                  :exposed-ports [port]
                  :env-vars      {"POSTGRES_DB"       dbname
                                  "POSTGRES_USER"     user
                                  "POSTGRES_PASSWORD" password}})
      (tc/bind-filesystem! {:host-path      "/tmp"
                            :container-path "/opt"
                            :mode           :read-only})
      (tc/start!)))

(defn stop!
  [container]
  (println "Stopping container")
  (tc/stop! container))

;; overriding :app.test/container
(defmethod ig/init-key :app.test/container
  [_ {:keys [db-spec]}]
  (when db-spec
    (postgres-container db-spec)))

(defmethod ig/halt-key! :app.test/container
  [_ container]
  (stop! container))
