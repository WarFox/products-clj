(ns app.orders.handlers
  (:require
   [app.domain :as domain]
   [app.orders.services :as service]))

(defn list-orders
  "Fetches all orders from the database"
  [{:keys [db]}]
  {:status 200
   :body   (service/get-orders db)})

(defn create-order
  "Creates a new order with items in the database"
  [{:keys [db body-params]}]
  {:status 201
   :body   (service/create-order db (domain/->Order body-params))})

(defn get-order
  "Fetches an order by ID from the database"
  [{:keys [db path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    {:status 200
     :body   (service/get-order db id)}))

(defn delete-order
  "Deletes an order by ID from the database"
  [{:keys [db path-params]}]
  (let [id (-> path-params :id parse-uuid)]
    (service/delete-order db id)
    {:status 204
     :body   nil}))

(defn update-order-status
  "Updates the status of an order"
  [{:keys [db path-params body-params]}]
  (let [id (-> path-params :id parse-uuid)
        status (:status body-params)]
    {:status 200
     :body   (service/update-order-status db id status)}))
