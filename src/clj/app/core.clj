(ns app.core
  (:gen-class)
  (:require
   [app.system :as system]
   [app.config :as config]))

(defonce system (atom nil))

(def defaults
  {:init       (fn []
                 (println "\n-=[app starting]=-"))
   :start      (fn []
                 (println "\n-=[app started successfully]=-"))
   :stop       (fn []
                 (println "\n-=[app has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (fn [thread ex]
   (println {:what      :uncaught-exception
             :exception ex
             :where     (str "Uncaught exception on" (.getName thread))})))

(defn stop-app []
  ((or (:stop defaults) (fn [])))
  (some-> (deref system) (system/halt!)))

(defn start-app [& [params]]
  (println "starting app", params)
  ((or (:start params) (:start defaults) (fn [])))
  (->> (config/system-config (or (:opts params) (:opts defaults) {}))
       (system/init)
       (reset! system)))

(defn -main [& args]
  ((or (:init defaults) (fn [])))
  (let [profile (if (some #{"--dev"} args) :dev :prod)]
    (start-app {:opts {:profile profile}}))
  (.addShutdownHook (Runtime/getRuntime) (Thread. (fn [] (stop-app) (shutdown-agents)))))
