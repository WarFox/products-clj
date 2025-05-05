(ns user
  (:require [integrant.repl :as ig-repl]
            [app.core :as app]
            [app.config :as config]))

(ig-repl/set-prep! (fn []  (config/config {:profile :dev})))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (go)
  (halt)
  (reset)
  (reset-all))
