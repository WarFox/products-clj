(ns frontend.components.forms)

(defn text-input
  [{:keys [value on-change placeholder label]}]
  [:div
   (when label
     [:label {:class "block text-sm font-medium text-gray-700"} label])
   [:input
    {:type "text"
     :value value
     :placeholder placeholder
     :class "mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
     :on-change #(on-change (-> % .-target .-value))}]])

(defn number-input
  [{:keys [value on-change label]}]
  [:div
   (when label
     [:label {:class "block text-sm font-medium text-gray-700"} label])
   [:input
    {:type "number"
     :step "0.01"
     :placeholder "0.00"
     :class "mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50"
     :value value
     :on-change #(on-change (-> % .-target .-valueAsNumber))}]])

(defn button
  [{:keys [on-click label class]}]
  [:button
   {:class (str "px-4 py-2 rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 " class)
    :on-click on-click}
   label])

(defn primary-button
  [on-click label]
  [button
   {:on-click on-click
    :label label
    :class "bg-indigo-600 text-white hover:bg-indigo-700 focus:ring-indigo-500"}])
