(ns frontend.views
  (:require
   [re-frame.core :as re-frame]
   [frontend.subs :as subs]
   [frontend.events :as events]
   [frontend.components.forms :as forms]
   [frontend.components.products :refer [products-grid]]))

(defn new-product-form
  [product]
  [:div {:class "bg-white shadow-md rounded-lg p-6 mb-8 max-w-md mx-auto"}
   [:h3 {:class "text-lg font-medium leading-6 text-gray-900 mb-4"} "Add a new product"]
   [:div {:class "space-y-4"}
    [forms/text-input
     {:label "Name"
      :value (:name product)
      :placeholder "Enter product name"
      :on-change #(re-frame/dispatch [::events/update-new-product :name %])}]

    [forms/number-input
     {:label "Price"
      :value (:price product)
      :on-change #(re-frame/dispatch [::events/update-new-product :price %])}]

    [forms/text-input
     {:label "Description"
      :value (:description product)
      :placeholder "Enter product description"
      :on-change #(re-frame/dispatch [::events/update-new-product :description %])}]

    [forms/primary-button
     #(re-frame/dispatch [::events/add-product product])
     "Add Product"]]])

(defn main-panel
  []
  (let [loading? (re-frame/subscribe [::subs/loading?])
        product  (re-frame/subscribe [::subs/new-product])
        products (re-frame/subscribe [::subs/products])]
    [:div {:class "min-h-screen bg-gray-100 py-6 px-4 sm:px-6 lg:px-8"}
     [:div {:class "max-w-7xl mx-auto"}
      [new-product-form @product]

      [:div {:class "bg-white shadow-md rounded-lg p-6"}
       [:div {:class "flex justify-between items-center mb-6"}
        [:h2 {:class "text-xl font-semibold text-gray-900"} "Products"]
        [forms/primary-button
         #(re-frame/dispatch [::events/load-products])
         "Load Products"]]

       (when @loading?
         [:div {:class "flex justify-center items-center py-4"}
          [:div {:class "animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"}]])

       [products-grid @products]]]]))
