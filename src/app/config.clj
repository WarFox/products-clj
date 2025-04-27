(ns app.config
  (:require [aero.core :as aero]
            [integrant.core :as ig]
            [clojure.java.io :as io]))

;; Custom reader for Integrant that allows us to use #ig/ref in our config file
(defmethod aero.core/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defmulti config
  "Read the configuration file."
  (fn [profile] profile))

(defmethod config :default
  [_]
  (aero/read-config
   (io/resource "config.edn")))

(defmethod config :test
  [_]
  (merge (config :default)
         (aero/read-config
          (io/resource "test-config.edn"))))

(defn db-spec [config]
  (get-in config [:db/spec]))
