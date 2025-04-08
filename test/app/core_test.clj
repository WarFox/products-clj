(ns app.core-test
  (:require [clojure.test :refer :all]
            [next.jdbc :as jdbc]
            [clj-test-containers.core :as tc]
            [app.core :refer :all]))

(deftest db-integration-test
  (testing "A simple PostgreSQL integration test"
    (let [pw "db-pass"
          postgres (-> (tc/create {:image-name    "postgres:14.1"
                                   :exposed-ports [5432]
                                   :env-vars      {"POSTGRES_PASSWORD" pw "POSTGRES_USER" "products"}}))
          container (tc/start! postgres)]

      (let [datasource (jdbc/get-datasource {:dbtype   "postgresql"
                                             :dbname   "products"
                                             :user     "products"
                                             :password pw
                                             :host     (:host postgres)
                                             :port     (get (:mapped-ports container) 5432)})]
        (is (= [{:one 1 :two 2}] (with-open [connection (jdbc/get-connection datasource)]
                                   (jdbc/execute! connection ["SELECT 1 ONE, 2 TWO"])))))
      (tc/stop! postgres))))
