(ns app.server
  (:require [ring.adapter.jetty :refer [run-jetty]]))

(defn start!
  [handler port]
  (run-jetty handler {:port port :join? false}))

(defn stop!
  [server]
  (.stop server))
