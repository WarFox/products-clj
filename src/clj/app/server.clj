(ns app.server
  (:require
   [integrant.core :as ig]
   [ring.adapter.jetty :refer [run-jetty]]))

(defn get-port
  [^org.eclipse.jetty.server.Handler server]
  (-> (.getConnectors server)
      (first)
      (.getLocalPort)))

(defn start!
  [handler port]
  (let [server (run-jetty handler {:port port :join? false})]
    (println "Server listening on port" (get-port server))
    server))

(defn stop!
  [server]
  (.stop server)
  (println "Server stopped"))

(defmethod ig/init-key :app.server/jetty
  [_ {:keys [handler port]}]
  (start! handler port))

(defmethod ig/halt-key! :app.server/jetty
  [_ server]
  (stop! server))
