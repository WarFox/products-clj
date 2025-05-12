(ns app.config
  (:require
   [aero.core :as aero]
   [integrant.core :as ig]
   [clojure.java.io :as io]))

;; Custom reader for Integrant that allows us to use #ig/ref in our config file
(defmethod aero.core/reader 'ig/ref
  [_opts _tag value]
  (ig/ref value))

(defn config
  [options]
  (aero/read-config
   (io/resource "config.edn") options))
