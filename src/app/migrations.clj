(ns app.migrations
  (:import [org.flywaydb.core Flyway]
           [org.flywaydb.core.api.configuration Configuration]))

(defn ^Configuration configuration
  [db]
  (-> (Flyway/configure)
      (.dataSource db)
      (.locations (into-array ["db/migrations"]))))

(defn ^Flyway flyway
  [db]
  (.load (configuration db)))

(defn migrate
  [db]
  (let [flyway (flyway db)]
    (println "Running migrations")
    (.migrate flyway)
    (println "Completed migrations")))
