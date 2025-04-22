(ns app.spec)

(def ProductV1
  [:map
   [:id uuid?]
   [:name :string]
   [:description :string]
   [:price-in-cents :int]
   [:created-at inst?]
   [:updated-at inst?]])

(def ProductV1Request
  [:map
   [:name :string]
   [:price-in-cents number?]
   [:description [:maybe :string]]])

(def ProductV1List
  [:vector [:ref #'ProductV1]])
