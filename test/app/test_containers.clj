(ns app.test-containers
  (:require
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
