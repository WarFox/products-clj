(ns app.containers
  "Container management namespace that handles containers for different environments"
  (:require
    [app.env :as env]
    [clojure.tools.logging :as log]
    [integrant.core :as ig]))

;; Container management using environment-specific implementation
(defmethod ig/init-key :app.containers/postgres-container
  [_ {:keys [db-spec env]}]
  (log/info "Initializing PostgreSQL container for environment:" env)
  (env/start-postgres-container! db-spec))

(defmethod ig/halt-key! :app.containers/postgres-container
  [_ container]
  (log/info "Stopping PostgreSQL container for environment:" (:env container))
  (env/stop-postgres-container! container))
