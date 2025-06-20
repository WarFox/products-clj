(ns app.test-containers
  "Container management namespace that handles containers for development"
  (:require
    [clj-test-containers.core :as tc]
    [clojure.tools.logging :as log]))
(defn start-postgres-container!
  "Creates and starts a PostgreSQL container for development environment"
  [{:keys [dbname user password port]}]
  (log/info "Starting PostgreSQL container")
  (let [container
        (-> (tc/create {:image-name    "postgres:14.1"
                        :exposed-ports [port]
                        :env-vars      {"POSTGRES_DB"       dbname
                                        "POSTGRES_USER"     user
                                        "POSTGRES_PASSWORD" password}})
            (tc/bind-filesystem! {:host-path      "/tmp"
                                  :container-path "/opt"
                                  :mode           :read-only})
            (tc/start!))]
    (assoc container
      :type :postgres-container)))

(defn stop-postgres-container!
  "Stops a PostgreSQL container"
  [container]
  (log/info "Stopping PostgreSQL container")
  (when container
    (tc/stop! container)))
