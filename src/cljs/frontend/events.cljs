(ns frontend.events
  (:require
   [re-frame.core :as re-frame]
   [frontend.db :as db]
   [frontend.domain :as domain]
   [ajax.core :as ajax]
   [day8.re-frame.http-fx]
   [camel-snake-kebab.core :as csk]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [cljs.spec.alpha :as s]))

(def backend-uri "http://localhost:3000/v1/products")

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

;; ;; add product
;; (re-frame/reg-event-db
;;   ::add-product
;;   (fn-traced
;;     [db [_ product]]
;;     (s/valid? ::domain/product product)
;;     (update db :products conj (assoc product :id (random-uuid) ))))

;; product form
(re-frame/reg-event-db
 ::update-new-product
 (fn-traced
  [db [_ k value]]
  (-> (assoc-in db [:new-product k] value)
        ;; assoc price-in-cents when price is updated
      (cond->  (= k :price)
        (assoc-in [:new-product :price-in-cents] (int (* 100 value)))))))

(re-frame/reg-event-db
 ::process-response
 (fn-traced
  [db [_ response]]
  (-> db
      (update :products conj (update-keys response csk/->kebab-case))
      (assoc :loading? false
             :new-product {}))))

(re-frame/reg-event-db
 ::bad-response
 (fn-traced
  [db [_ response]]
  (update db :errors conj response)))

;; add product
(re-frame/reg-event-fx
 ::add-product
 (fn-traced
  [{:keys [db]} [_ product]]
  (s/valid? ::domain/product product)
  {;; we return a map of (side) effects
   :http-xhrio {:method          :post
                :uri            backend-uri
                :params          (update-keys product csk/->camelCase)
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [::process-response]
                :on-failure      [::bad-response]}

   :db (assoc db :loading? true)}))

(re-frame/reg-event-db
 ::process-load-products-response
 (fn-traced
  [db [_ response]]
  (-> db
      (assoc  ;;:products (fn [_] (map #(update-keys % csk/->kebab-case) response))
       :products (map #(update-keys % csk/->kebab-case) response)
       :loading? false))))

(re-frame/reg-event-fx
 ::load-products
 (fn-traced
  [{:keys [db]} _]
  {;; we return a map of (side) effects
   :http-xhrio {:method          :get
                :uri             backend-uri
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [::process-load-products-response]
                :on-failure      [::bad-response]}

   :db (assoc db :loading? true)}))

(re-frame/reg-event-fx
 ::delete-product
 (fn-traced
  [{:keys [db]} [_ id]]
  {;; we return a map of (side) effects
   :http-xhrio {:method          :delete
                :uri             (str backend-uri "/" id)
                :format          (ajax/json-request-format)
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success      [::load-products]
                :on-failure      [::bad-response]}}))
