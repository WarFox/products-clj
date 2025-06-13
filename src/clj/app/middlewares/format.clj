(ns app.middlewares.format
  (:require
   [camel-snake-kebab.core :as csk]
   [muuntaja.core :as m]))

(defonce instance
  (m/create
    (->
      m/default-options
      (assoc-in
        [:formats "application/json" :decoder-opts] ;; decode request
        {:decode-key-fn csk/->kebab-case-keyword})
      (assoc-in
        [:formats "application/json" :encoder-opts] ;; encode response
        {:encode-key-fn csk/->camelCaseString}))))
