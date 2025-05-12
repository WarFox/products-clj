(ns app.malli.registry
  (:require
   [app.spec]
   [malli.core :as m]
   [malli.experimental.time :as met]
   [malli.registry :as mr]))

(mr/set-default-registry!
 (mr/composite-registry
  (m/default-schemas)
  (met/schemas))) ;; :time/instant
