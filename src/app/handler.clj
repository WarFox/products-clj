(ns app.handler
  (:require [app.products :as products]
            [camel-snake-kebab.core :as csk]
            [muuntaja.core :as m]
            [reitit.coercion.malli :as malli]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [reitit.ring.spec :as rrs]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
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
     [:formats "application/json" :encoder-opts] ;; encode response
     {:encode-key-fn csk/->camelCaseString})

    (assoc-in
     [:formats "application/json" :decoder-opts] ;; decode request
     {:decode-key-fn csk/->kebab-case-keyword}))))

(defn handler
  [db]
  (ring/ring-handler
   (ring/router
      ;; paths
    routes
      ;; options
    {:validate rrs/validate
     :data     {:db         db
                :muuntaja   muuntaja-instance
                :coercion   malli/coercion
                  ;; order of middldewares matter
                :middleware [[wrap-cors :access-control-allow-origin [#"http://localhost:8280"]
                                        :access-control-allow-methods [:get :post :put :delete]]
                             wrap-keyword-params
                             parameters-middleware
                             muuntaja/format-middleware
                             coercion/coerce-exceptions-middleware
                             coercion/coerce-request-middleware
                             coercion/coerce-response-middleware
                             db-middleware
                             stacktrace/wrap-stacktrace-log]}})
   (ring/routes
    (ring/create-default-handler))))

(comment
  (r/router-name
   (ring/router routes {:router r/linear-router}))

  (r/route-names (ring/router routes))
  (r/match-by-path (ring/router routes) "/v1/products"))
