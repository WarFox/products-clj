(ns app.orders.services
  "Service functions to work with Order domain, has business logic and validation"
  (:require
   [app.spec :as spec]
   [malli.core :as malli]
   [malli.error :as me]
   [integrant.core :as ig])
  (:import
   (clojure.lang ExceptionInfo)
   (java.util UUID)))

(defn create-order-with-items
  "Creates a new order with items in the database"
  [repository order]
  (try
    (malli/assert spec/OrderV1 order)
    ((:create-order-with-items repository) order)
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
  [repository]
  (try
    (let [orders ((:get-orders repository))]
      ;; For each order, fetch its items
      (mapv #(assoc % :items ((:get-order-items repository) (:id %))) orders))
    (catch Exception e
      (throw (ex-info "Failed to get orders"
                      {:type  :system.exception/internal
                       :cause e})))))

(defn get-order
  "Fetches an order by ID from the database"
  [repository ^UUID id]
  (try
    (let [result ((:get-order repository) id)]
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
  [repository ^UUID id]
  (try
    (let [result ((:delete-order repository) id)]
      (when (zero? result)
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
  [repository ^UUID id status]
  (try
    (malli/assert spec/OrderStatusEnum status)
    ;; Check if order exists first
    (get-order repository id)
    ((:update-order-status repository) id status)
    (catch ExceptionInfo e
      (throw e))                                           ; Pass through our custom exceptions
    (catch Exception e
      (throw (ex-info "Failed to update order status"
                      {:type     :system.exception/internal
                       :order-id id
                       :status   status
                       :cause    e})))))

(defmethod ig/init-key :app.orders/service
  [_ {:keys [repository]}]
  {:create-order-with-items (partial create-order-with-items repository)
   :get-orders              (partial get-orders repository)
   :get-order               (partial get-order repository)
   :delete-order            (partial delete-order repository)
   :update-order-status     (partial update-order-status repository)})
