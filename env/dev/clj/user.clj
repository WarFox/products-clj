(ns user
  (:require [app.config :as config]
            [integrant.core :as ig]
            [integrant.repl :as ig-repl]))

(defn dev-prep!
  []
  (ig-repl/set-prep!
    (fn []
      (-> (config/system-config {:profile :dev})
          (ig/expand)))))

(dev-prep!)

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
