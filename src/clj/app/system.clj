(ns app.system
  (:require
   [app.db]
   [app.handler]
   [app.migrations]
   [app.server]
   [integrant.core :as ig]))

(defmethod ig/init-key :app.system/env
  [_ env]
  env)

;; We don't need test container in production
(defmethod ig/init-key :app.test/container
  [_ _]
  {:type         :dummy-container
   :mapped-ports {}})

;; Try to load the real test container implementation if available
(try
  (require 'app.test-containers)
  (println "-=Loaded test containers for development mode=-")
  (catch Exception _
    (println "Test containers not available - using dummy implementation")))

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
