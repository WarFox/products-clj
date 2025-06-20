(ns app.core
  (:gen-class)
  (:require
    [app.config :as config]
    [app.env :as env]
    [app.system :as system]
    [clojure.tools.logging :as log]))

(defonce system (atom nil))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (fn [thread ex]
    (println {:what      :uncaught-exception
              :exception ex
              :where     (str "Uncaught exception on" (.getName thread))})))

(defn stop-app
  [defaults]
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (system/halt!)))

(defn start-app
  [defaults & [params]]
  (let [opts (or (:opts params) (:opts defaults) {})]
    (log/info "Starting app \uD83C\uDF89 opts:", opts, "params:" params)
    ((or (:init params) (:init defaults) (fn [])))
    (->> (config/system-config opts)
         (system/init)
         (reset! system)))
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (stop-app defaults) (shutdown-agents)))))

(defn -main
  [& args]
  (start-app env/defaults args))
