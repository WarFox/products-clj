(ns frontend.components.products
  (:require
   [goog.string :as gstring]
   [re-frame.core :as re-frame]
   [frontend.events :as events]))

(defn product-card
  [{:keys [id name description price-in-cents]}]
  [:div {:class "relative bg-white rounded-lg border border-gray-200 p-4 hover:shadow-lg transition-shadow"}
   [:div {:class "flex justify-between items-start"}
    [:div
     [:h3 {:class "text-lg font-medium text-gray-900"} name]
     [:p {:class "mt-1 text-sm text-gray-500"} description]
     [:p {:class "mt-2 text-lg font-semibold text-indigo-600"}
      "$" (gstring/format "%.2f" (/ price-in-cents 100.00))]]
    [:button {:class "text-gray-400 hover:text-red-500 focus:outline-none"
              :on-click #(re-frame/dispatch [::events/delete-product id])}
     [:svg {:class "h-5 w-5"
            :xmlns "http://www.w3.org/2000/svg"
            :viewBox "0 0 20 20"
            :fill "currentColor"}
      [:path {:fill-rule "evenodd"
              :d "M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
              :clip-rule "evenodd"}]]]]])

(defn products-grid
  [products]
  [:div {:class "grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3"}
   (for [product products]
     ^{:key (:id product)}
     [product-card product])])
