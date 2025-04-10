(ns app.handler
  (:require [reitit.ring :as ring]
            [reitit.ring.spec :as rrs]
            [reitit.core :as r]
            [app.products :as products]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [ring.middleware.json :refer [wrap-json-response]]))

(def routes
  [["/v1" {:name :version1}
    products/routes]])

(def middleware-db
  {:name    ::db
   :compile (fn [{:keys [db]} _]
              (fn [handler]
                (fn [req]
                  (handler (assoc req :db db)))))})

(defn handler
  [db]
  (ring/ring-handler
   (ring/router
    routes
    {:validate rrs/validate
     :data     {:db         db
                :middleware [middleware-db
                             parameters-middleware
                             wrap-keyword-params
                             wrap-json-response]}})))

(comment
  (r/route-names (ring/router routes))
  (r/match-by-path (ring/router routes) "/v1/products"))
