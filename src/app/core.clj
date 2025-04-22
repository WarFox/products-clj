(ns app.core
  (:gen-class)
  (:require [app.system :as system]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (system/init {}))
