(ns app.router
  (:require
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.spec :as rrs]
   [reitit.coercion.malli :as malli]
   [ring.middleware.cors :as cors]
   [ring.middleware.keyword-params :as params]
   [ring.middleware.stacktrace :as stacktrace]
   [app.middlewares.exception :as exception]
   [app.middlewares.format :as format]
   [app.middlewares.response :as response]))

(defmethod ig/init-key :app.router/routes
  [_ {:keys [routes]}]
  ["/v1" {:name :version1}
   routes])

(defmethod ig/init-key :app.router/core
  [_ {:keys [routes]}]
  (ring/router
   routes
   {:validate rrs/validate
    :data     {:muuntaja   format/instance             ; Use the customized muuntaja instance
               :coercion   malli/coercion              ; Use Malli coercion for Reitit
               :middleware [;; Ensure correct order, muuntaja format-middleware before coercion
                            [cors/wrap-cors
                             :access-control-allow-origin [#"http://localhost:8280"]
                             :access-control-allow-methods [:get :post :put :delete]]
                            params/wrap-keyword-params
                            parameters/parameters-middleware ;; Handles query/form params
                            muuntaja/format-negotiate-middleware ;; Handles content negotiation
                            muuntaja/format-response-middleware ;; Encodes response body
                            muuntaja/format-request-middleware ;; Decodes request body
                            response/wrap-response-envelope ;; Wrap successful responses in envelope
                            coercion/coerce-request-middleware ;; Coerces request parameters
                            coercion/coerce-response-middleware ;; Coerces response body
                            stacktrace/wrap-stacktrace-log
                            exception/wrap-exception]}}))

(defmethod ig/init-key :app.handler/ring
  [_ {:keys [router]}]
  (ring/ring-handler
   router
   (ring/routes
    (ring/create-default-handler))))
