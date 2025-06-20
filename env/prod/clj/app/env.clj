(ns app.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[ starting]=-"))
   :started    (fn []
                 (log/info "\n-=[ started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[ has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})

(defn start-postgres-container!
  "Production implementation that returns a dummy container (no-op)"
  [_]
  (log/info "Using real PostgreSQL database in production (no container)")
  {:type :dummy-container
   :mapped-ports {}})

(defn stop-postgres-container!
  "No-op implementation for production"
  [_]
  nil)
