(ns app.products-test
  (:import [java.time Instant])
  (:require [app.products :as products]
            [app.system :as system]
            [clojure.test :as t]
            [app.containers :as containers]
            [app.db :as db]))

(def ^:dynamic *db* (atom nil))

(t/use-fixtures :once
  (fn [f]
    (let [container (containers/postgres-container system/db-spec)
          port      (-> container :mapped-ports (get 5432))
          db-spec   (assoc system/db-spec :port port)]
      (reset! *db* db-spec)
      (db/create-table db-spec)
      (f)
      (containers/stop! container))))

(t/deftest create-product-test
  (t/testing "Create a product"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}
          request {:db          @*db*
                   :body-params product}]
      (t/is (= {:status 201
                :body   product}
               (products/create-product request))))))

(t/deftest get-products-test
  (t/testing "Get all products"
    (let [product {:id             (random-uuid)
                   :name           "Test Product"
                   :description    "This is a test product"
                   :price-in-cents 100
                   :created-at     (Instant/now)
                   :updated-at     (Instant/now)}]
      (products/create-product {:db @*db*
                           :body-params product}) ; Create a product for testing
      (t/is (= {:status 200
                :body   [product]}
               (products/get-products {:db @*db*}))))))
