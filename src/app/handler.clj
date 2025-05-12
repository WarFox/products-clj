(ns app.handler
  (:require [app.products :as products]
            [app.malli.registry] ;; enable registry
            [camel-snake-kebab.core :as csk]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.spec :as rrs]
            [ring.middleware.cors :as cors]
            [ring.middleware.keyword-params :as params]
            [ring.middleware.stacktrace :as stacktrace]))

(def routes
  [["/v1" {:name :version1}
    products/routes]])

(def db-middleware
  {:name    ::db
   :compile (fn [{:keys [db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :db db)))))})

(defonce muuntaja-instance
  (m/create
   (->
    m/default-options
    (assoc-in
     [:formats "application/json" :decoder-opts] ;; decode request
     {:decode-key-fn csk/->kebab-case-keyword})
    (assoc-in
     [:formats "application/json" :encoder-opts] ;; encode response
     {:encode-key-fn csk/->camelCaseString}))))

(defn handler
  [db]
  (ring/ring-handler
   (ring/router
    routes
    {:validate rrs/validate
     :data     {:db         db
                :muuntaja   muuntaja-instance ; Use the customized instance
                :coercion   malli/coercion ; Use Malli coercion for Reitit
                :middleware [;; Ensure correct order, muuntaja format-middleware before coercion
                             [cors/wrap-cors
                              :access-control-allow-origin [#"http://localhost:8280"]
                              :access-control-allow-methods [:get :post :put :delete]]
                             params/wrap-keyword-params
                             parameters/parameters-middleware ;; Handles query/form params
                             muuntaja/format-negotiate-middleware ;; Handles content negotiation
                             muuntaja/format-response-middleware  ;; Encodes response body
                             muuntaja/format-request-middleware   ;; Decodes request body
                             coercion/coerce-exceptions-middleware
                             coercion/coerce-request-middleware   ;; Coerces request parameters
                             coercion/coerce-response-middleware  ;; Coerces response body
                             db-middleware
                             stacktrace/wrap-stacktrace-log]}})
   (ring/routes
    (ring/create-default-handler))))

(comment
  (r/router-name
   (ring/router routes {:router r/linear-router}))

  (r/route-names (ring/router routes))
  (r/match-by-path (ring/router routes) "/v1/products"))
