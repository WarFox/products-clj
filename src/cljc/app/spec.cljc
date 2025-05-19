(ns app.spec)

(def ProductV1
  [:map {:closed true}
   [:id uuid?]
   [:name :string]
   [:description :string]
   [:price-in-cents :int]
   [:created-at :time/instant]
   [:updated-at :time/instant]])

(def ProductV1Request
  [:map {:closed true}
   [:name :string]
   [:price-in-cents number?]
   [:description [:maybe :string]]])

(def ProductV1List
  [:vector ProductV1])
