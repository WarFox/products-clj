(ns app.containers
  (:require
   [clj-test-containers.core :as tc]))

(defn postgres-container
  [port]
  (println "Starting container")
  (-> (tc/create {:image-name    "postgres:14.1"
                  :exposed-ports [port]
                  :env-vars      {"POSTGRES_PASSWORD" "verysecret"
                                  "POSTGRES_USER"     "products"
                                  "POSTGRES_DB"       "products"}})
      (tc/bind-filesystem! {:host-path      "/tmp"
                            :container-path "/opt"
                            :mode           :read-only})
      (tc/start!)))
