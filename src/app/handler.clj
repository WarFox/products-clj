(ns app.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.spec :as rrs]
            [reitit.core :as r]
            [app.products :as products]
            [camel-snake-kebab.core :as csk]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]))

(def routes
  [["/v1" {:name :version1}
    products/routes]])

(def middleware-db
  {:name    ::db
   :compile (fn [{:keys [db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :db db)))))})

(def json-request-body
  {:name ::json-body
   :wrap #(wrap-json-body % {:keywords? true
                             :key-fn csk/->kebab-case-keyword})})

(def json-response-body
  {:name ::json-response
   :wrap #(wrap-json-response % {:key-fn csk/->camelCaseString})})

(def cors-middleware
  {:name ::cors-middleware
   :wrap #(wrap-cors %
                     :access-control-allow-origin [#"http://localhost:8280"]
                     :access-control-allow-methods [:get :post :put :delete])})

(defn handler
  [db]
  (ring/ring-handler
   (ring/router
    routes
    {:validate rrs/validate
     :data     {:db         db
                :middleware [cors-middleware
                             json-request-body
                             json-response-body
                             middleware-db
                             parameters-middleware
                             wrap-keyword-params]}})))

(comment
  (r/route-names (ring/router routes))
  (r/match-by-path (ring/router routes) "/v1/products"))
