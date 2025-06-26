(ns app.spec)

(def ProductV1
  [:map {:closed true}
   [:id :uuid]
   [:name :string]
   [:description :string]
   [:price-in-cents [:int {:min 0}]]
   [:created-at :time/instant]
   [:updated-at :time/instant]])

(def ProductV1Request
  [:map {:closed true}
   [:name :string]
   [:price-in-cents number?]
   [:description [:maybe :string]]])

(def ProductV1List
  [:vector ProductV1])

(def OrderStatusEnum
  [:enum "pending" "processing" "shipped" "delivered" "cancelled"])

(def OrderItemV1
  [:map {:closed true}
   [:id :uuid]
   [:order-id :uuid]
   [:product-id :uuid]
   [:quantity [:int {:min 1}]]
   [:price-per-unit [:int {:min 0}]]])

(def OrderItemV1Request
  [:map {:closed true}
   [:product-id :uuid]
   [:quantity [:int {:min 0}]]
   [:price-per-unit [:int {:min 0}]]])

(def OrderV1
  [:map {:closed true}
   [:id :uuid]
   [:customer-name :string]
   [:customer-email :string]
   [:status OrderStatusEnum]
   [:total-amount :int]
   [:shipping-address :string]
   [:created-at :time/instant]
   [:updated-at :time/instant]
   [:items [:vector OrderItemV1]]])

(def OrderV1Request
  [:map {:closed true}
   [:customer-name :string]
   [:customer-email :string]
   [:shipping-address :string]
   [:items [:vector OrderItemV1Request]]])

(def OrderV1List
  [:vector OrderV1])
