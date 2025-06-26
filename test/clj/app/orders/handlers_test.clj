(ns app.orders.handlers-test
  (:require
   [app.orders.repository :as repository]
   [app.orders.handlers :as handler]
   [app.spec :as spec]
   [app.test-system :as test-system]
   [app.util.time :as time]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-db]]
   [malli.core :as malli]
   [malli.error :as me]))

(use-fixtures :once
  with-db)

(use-fixtures :each
  truncate-table)

(deftest create-order-test
  (testing "Create a order"
    (let [order {:name           "Test Order"
                   :description    "This is a test order"
                   :price-in-cents 100}
          request {:db          @test-system/*db*
                   :body-params order}]
      (is (malli/validate spec/OrderV1Request order) (-> spec/OrderV1Request (malli/explain order) (me/humanize)))
      (let [{:keys [status body]} (handler/create-order request)
            expected              (assoc order
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
    (let [orders [{:id             (random-uuid)
                     :name           "Test Order 1"
                     :description    "Hello, this is a test order"
                     :price-in-cents 100
                     :created-at     (time/instant-now :micros)
                     :updated-at     (time/instant-now :micros)}
                    {:id             (random-uuid)
                     :name           "Test Order 2"
                     :description    "Hello, this is a test order 2"
                     :price-in-cents 200
                     :created-at     (time/instant-now :micros)
                     :updated-at     (time/instant-now :micros)}]]
      (doseq [order orders]
        (repository/create-order @test-system/*db* order)) ; Create a order for testing
      (is (= {:status 200
              :body   orders}
             (handler/list-orders {:db @test-system/*db*}))))))

(deftest get-order-by-id
  (testing "Get order by id"
    (let [order {:id             (random-uuid)
                   :name           "Test Order"
                   :description    "This is a test order"
                   :price-in-cents 100
                   :created-at     (time/instant-now :micros)
                   :updated-at     (time/instant-now :micros)}]
      (repository/create-order @test-system/*db* order) ; Create a order for testing
      (is (= {:status 200
              :body   order}
             (handler/get-order {:db          @test-system/*db*
                                   :path-params {:id (str (:id order))}}))))))
