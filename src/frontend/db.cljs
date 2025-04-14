(ns frontend.db)

(def default-db
  {:loading?    false
   :new-product {:name "" :price-in-cents 0 :description ""}
   :products    []})
