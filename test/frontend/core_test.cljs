(ns frontend.core-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
