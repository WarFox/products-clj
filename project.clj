(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [camel-snake-kebab "0.4.3"]
                 [com.fasterxml.jackson.core/jackson-databind "2.18.3"]
                 [com.github.seancorfield/next.jdbc "1.3.1002"]
                 [com.widdindustries/cljc.java-time "0.1.21"]
                 [integrant/integrant "0.13.1"]
                 [integrant/repl  "0.4.0"]
                 [metosin/muuntaja "0.6.11"]
                 [metosin/reitit-malli "0.8.0"]
                 [metosin/reitit-middleware "0.8.0"]
                 [metosin/reitit-ring "0.8.0"]
                 [org.clojure/clojure "1.12.0"]
                 [org.flywaydb/flyway-database-postgresql "11.8.0"]
                 [org.postgresql/postgresql "42.7.5"]
                 [ring-cors "0.1.13"]
                 [ring/ring-core "1.14.1"]
                 [ring/ring-devel "1.14.1"]
                 [ring/ring-jetty-adapter "1.14.1"]]
  :main ^:skip-aot app.core
  :plugins [[lein-ring "0.12.6"]
            [dev.weavejester/lein-cljfmt "0.13.1"]]
  :target-path "target/%s"
  :ring {:handler app.handler/handler}
  :source-paths ["src/clj" "src/cljc" "test/clj" "test/cljc"]
  :profiles {:uberjar
             {:aot      :all
              :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev
             {:dependencies [[cheshire "6.0.0"]
                             [clj-http "3.13.0"]
                             [clj-kondo "2025.04.07"]
                             [clj-test-containers "0.7.4"]
                             [org.clojure/data.json "2.5.1"]]
              :aliases {"clj-kondo" ["run" "-m" "clj-kondo.main"]}}})
