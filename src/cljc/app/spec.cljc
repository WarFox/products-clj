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
   [:price-in-cents [:int {:min 0}]]
   [:description :string]])

(def ProductV1List
  [:sequential ProductV1])

(def ProductV1Response
  [:map {:closed true}
   [:id :string]
   [:name :string]
   [:priceInCents [:int {:min 0}]]
   [:description [:maybe :string]]
   [:createdAt :string]
   [:updatedAt :string]])

(def ProductV1ListResponse
  [:sequential ProductV1Response])

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
   [:quantity [:int {:min 1 :max 1000}]]
   [:price-per-unit [:int {:min 0 :max 9999}]]])

(def OrderV1
  [:map {:closed true}
   [:id :uuid]
   [:customer-name :string]
   [:customer-email :string]
   [:status OrderStatusEnum]
   [:total-amount [:int {:min 0}]]
   [:shipping-address :string]
   [:created-at :time/instant]
   [:updated-at :time/instant]
   [:items [:sequential OrderItemV1]]])

(def OrderV1Request
  [:map {:closed true}
   [:customer-name :string]
   [:customer-email :string]
   [:shipping-address :string]
   [:items [:sequential OrderItemV1Request]]])

(def OrderV1List
  [:sequential OrderV1])

(def OrderItemV1Response
  [:map {:closed true}
   [:id :string]
   [:orderId :string]
   [:productId :string]
   [:quantity [:int {:min 1}]]
   [:pricePerUnit [:int {:min 0}]]])

(def OrderV1Response
  [:map {:closed true}
   [:id :string]
   [:customerName :string]
   [:customerEmail :string]
   [:status OrderStatusEnum]
   [:totalAmount [:int {:min 0}]]
   [:shippingAddress :string]
   [:createdAt :string]
   [:updatedAt :string]
   [:items [:sequential OrderItemV1Response]]])

(def OrderV1ListResponse
  [:sequential OrderV1Response])
