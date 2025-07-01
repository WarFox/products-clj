(ns app.orders.services
  "Service functions to work with Order domain, has business logic and validation"
  (:require
   [app.orders.repository :as order-repo]
   [app.spec :as spec]
   [malli.core :as malli]
   [malli.error :as me])
  (:import
   (clojure.lang ExceptionInfo)
   (java.util UUID)))

(defn create-order-with-items
  "Creates a new order with items in the database"
  [db order]
  (try
    (malli/assert spec/OrderV1 order)
    (order-repo/create-order-with-items db order)
    (catch ExceptionInfo e
      (throw (ex-info "Invalid order data"
                      {:type   :system.exception/business
                       :order  order
                       :explain (-> spec/OrderV1 (malli/explain order) me/humanize)
                       :cause  e})))
    (catch Exception e
      (throw (ex-info "Failed to create order"
                      {:type   :system.exception/internal
                       :order  order
                       :cause  e})))))

(defn get-orders
  "Fetches all orders from the database"
  [db]
  (try
    (let [orders (order-repo/get-orders db)]
      ;; For each order, fetch its items
      (mapv #(assoc % :items (order-repo/get-order-items db (:id %))) orders))
    (catch Exception e
      (throw (ex-info "Failed to get orders"
                      {:type  :system.exception/internal
                       :cause e})))))

(defn get-order
  "Fetches an order by ID from the database"
  [db ^UUID id]
  (try
    (let [result (order-repo/get-order db id)]
      (if (nil? result)
        (throw (ex-info "Order not found"
                        {:type     :system.exception/not-found
                         :order-id id}))
        result))
    (catch ExceptionInfo e
      (throw e))                                           ; Pass through our custom exceptions
    (catch Exception e
      (throw (ex-info "Failed to get order"
                      {:type     :system.exception/internal
                       :order-id id
                       :cause    e})))))

(defn delete-order
  "Deletes an order by ID from the database"
  [db ^UUID id]
  (try
    (let [result (order-repo/delete-order db id)]
      (if (= 0 result)
        (throw (ex-info "Order not found"
                        {:type     :system.exception/not-found
                         :order-id id})))
      result)
    (catch ExceptionInfo e
      (throw e))                                           ; Pass through our custom exceptions
    (catch Exception e
      (throw (ex-info "Failed to delete order"
                      {:type     :system.exception/internal
                       :order-id id
                       :cause    e})))))

(defn update-order-status
  "Updates the status of an order by ID"
  [db ^UUID id status]
  (try
    (malli/assert spec/OrderStatusEnum status)
    ;; Check if order exists first
    (get-order db id)
    (order-repo/update-order-status db id status)
    (catch ExceptionInfo e
      (throw e))                                           ; Pass through our custom exceptions
    (catch Exception e
      (throw (ex-info "Failed to update order status"
                      {:type     :system.exception/internal
                       :order-id id
                       :status   status
                       :cause    e})))))
