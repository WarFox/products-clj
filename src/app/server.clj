(ns app.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.logger :as logger]))

(defn start!
  [handler port]
  (run-jetty (logger/wrap-with-logger handler) {:port port :join? false}))

(defn stop!
  [server]
  (.stop server))
