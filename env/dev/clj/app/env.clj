(ns app.env
  (:require
    [clojure.tools.logging :as log]
    [app.test-containers :as test-containers]))

(def defaults
  {:init       (fn []
                 (log/info "-=[ starting using the development profile]=-"))
   :started    (fn []
                 (log/info "-=[ started successfully using the development profile]=-"))
   :stop       (fn []
                 (log/info "-=[ has shut down successfully]=-"))
   :opts       {:profile       :dev
                :persist-data? true}})

(defn start-postgres-container!
  [db-spec]
  (test-containers/start-postgres-container! db-spec))

(defn stop-postgres-container!
  [container]
  (test-containers/stop-postgres-container! container))
