(ns frontend.domain
  (:require
   [cljs.spec.alpha :as s]))

(s/def ::id uuid?)
(s/def ::name string?)
(s/def ::description string?)
(s/def ::price number?)
(s/def ::created-at keyword?)
(s/def ::updated-at keyword?)

(s/def ::product
  (s/keys :req-un [::id ::name ::price]
          :opt-un [::description ::created-at ::updated-at]))
