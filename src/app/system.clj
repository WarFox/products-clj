(ns app.system
  (:require
   [app.db]
   [app.handler]
   [app.migrations]
   [app.server]
   [app.test-containers :as tc]
   [integrant.core :as ig]))

(defmethod ig/init-key :app.system/env
  [_ env]
  env)

(defmethod ig/init-key :app.test/container
  [_ {:keys [db-spec]}]
  (when db-spec
    (tc/postgres-container db-spec)))

(defmethod ig/halt-key! :app.test/container
  [_ container]
  (tc/stop! container))

(defn init
  "Initialize the system."
  ([config]
   (-> config
       (ig/expand)
       (ig/init)))
  ([config opts]
   (-> config
       (ig/expand)
       (ig/init opts))))

(defn halt!
  "Halt the system."
  [system]
  (ig/halt! system))
