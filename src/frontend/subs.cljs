(ns frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::products
 (fn [db]
    (:products db)))

(re-frame/reg-sub
  ::new-product
  (fn [db]
    (:new-product db)))

(re-frame/reg-sub
  ::loading?
  (fn [db]
    (:loading? db)))
