(ns app.core
  (:gen-class)
  (:require [app.system :as system]
            [app.config :as config]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting application")
  (let [config (config/config :default)]
    (system/init config)))
