(ns app.system
  (:require
   [app.containers]
   [app.db]
   [app.handler]
   [app.migrations]
   [app.server]
   [integrant.core :as ig]))

(defmethod ig/init-key :app.system/env
  [_ env]
  env)

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
