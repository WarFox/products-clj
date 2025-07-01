(ns app.orders.handlers-test
  (:require
   [app.orders.handlers :as handler]
   [app.orders.repository :as repository]
   [app.spec :as spec]
   [app.test-system :refer [db]]
   [app.util.time :as time]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-db]]
   [generators :refer [generate-order generate-product]]
   [malli.core :as malli]
   [malli.error :as me]
   [malli.generator :as mg]
   [app.products.services :as product-service]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-order-test
  (testing "Create a order"
    (let [order-request (mg/generate spec/OrderV1Request)
          request       {:db          (db)
                         :body-params order-request}]
      (is (malli/validate spec/OrderV1Request order-request) (-> spec/OrderV1Request (malli/explain order-request) (me/humanize)))
      (let [{:keys [status body]} (handler/create-order request)
            expected              (assoc order-request
                                         :created-at (:created-at body)
                                         :updated-at (:updated-at body)
                                         :id (:id body))]
        (is (= 201 status))
        (is (= expected body))
        (is (uuid? (:id body)))
        (is (inst? (:created-at body)))
        (is (inst? (:updated-at body)))
        (is (= (:created-at body) (:updated-at body)))))))

(deftest get-orders-test
  (testing "Get all orders"
    (let [orders (take 2 (repeatedly generate-order))
          items  (flatten (map :items orders))]
      (doseq [item items]
        (product-service/create-product
         (db)
         (generate-product (:product-id item))))
      (doseq [order orders]
        (repository/create-order-with-items (db) order)) ; Create a order for testing
      (is (= {:status 200
              :body   orders}
             (handler/list-orders {:db (db)}))))))

(deftest get-order-by-id
  (testing "Get order by id"
    (let [order {:id             (random-uuid)
                 :name           "Test Order"
                 :description    "This is a test order"
                 :price-in-cents 100
                 :created-at     (time/instant-now :micros)
                 :updated-at     (time/instant-now :micros)}]
      (repository/create-order-with-items (db) order) ; Create a order for testing
      (is (= {:status 200
              :body   order}
             (handler/get-order {:db          (db)
                                 :path-params {:id (str (:id order))}}))))))
