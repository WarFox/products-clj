(ns app.system
  (:require
   [app.containers]
   [app.db]
   [app.migrations]
   [app.server]
   [app.router]
   [app.malli.registry]
   [app.products.repository]
   [app.products.services]
   [app.products.handlers]
   [app.products.routes]
   [app.orders.repository]
   [app.orders.services]
   [app.orders.handlers]
   [app.orders.routes]
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
