(ns app.products.integration-test
  (:require
   [api-helpers :as api]
   [app.products.repository :as product-repo]
   [app.spec :as spec]
   [app.test-system :refer [db]]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [fixtures :refer [truncate-table with-system given-product]]
   [generators :refer [generate-product generate-product-request]]
   [malli.core :as malli]))

(use-fixtures :once
  with-system)

(use-fixtures :each
  truncate-table)

(deftest create-product-integration-test
  (testing "Create a product via API"
    (let [product               (generate-product-request)
          response              (api/create-product! product)
          {:keys [status body]} response]
      (malli/assert spec/ProductV1Response body)
      (is (= 201 status))
      (is (uuid? (parse-uuid (:id body))))
      (is (= (:name product) (:name body)))
      (is (= (:description product) (:description body)))
      (is (= (:price-in-cents product) (:priceInCents body)))
      (is (= (:updatedAt body) (:createdAt body))))))

(deftest get-products-integration-test
  (testing "Get all products via API"
    (let [product1 (generate-product-request)
          product2 (generate-product-request)]
      ;; Create test products via API
      (api/create-product! product1)
      (api/create-product! product2)

      (let [response (api/get-products)
            {:keys [status body]} response]
        (malli/assert spec/ProductV1ListResponse body)
        (is (= 200 status))
        (is (= 2 (count body)))
        ;; Verify the products are returned (order might differ)
        (let [returned-names (set (map :name body))]
          (is (contains? returned-names (:name product1)))
          (is (contains? returned-names (:name product2))))))))

(deftest get-product-by-id-integration-test
  (testing "Get product by id via API"
    (let [product               (given-product (generate-product))
          product-request       (generate-product-request product)
          response              (api/get-product (:id product))
          {:keys [status body]} response]
      (malli/assert spec/ProductV1Response body)
      (is (= 200 status))
      (is (= (str (:id product)) (:id body)))
      (is (= (:name product-request) (:name body)))
      (is (= (:description product-request) (:description body)))
      (is (= (:price-in-cents product-request) (:priceInCents body)))
      (is (= (str (:created-at product)) (:createdAt body))))))

(deftest get-product-by-id-not-found-integration-test
  (testing "Get product by non-existent id returns 404"
    (let [non-existent-id (random-uuid)
          response (api/get-product non-existent-id)
          {:keys [status]} response]
      (is (= 404 status)))))

(deftest delete-product-integration-test
  (testing "DELETE request to server to delete a product by ID"
    (let [product  (given-product (generate-product))
          response (api/delete-product! (:id product))
          {:keys [status body]} response]
      (is (= 204 status))
      (is (= nil body))
      (is (nil? (product-repo/get-product (db) (:id product)))))))

(deftest update-product-test
  (testing "Send PUT request to server to update a product"
    (let [product (given-product (generate-product))
          updated-product-data {:name "Updated Product"
                                :description "Updated Description"
                                :price-in-cents 200}
          response (api/update-product! (:id product) updated-product-data)
          {:keys [status body]} response]
      (is (= 200 status))
      (is (= (:name updated-product-data) (:name body))))))
