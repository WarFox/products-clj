(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[clj-test-containers "0.7.4"]
                 [com.github.seancorfield/next.jdbc "1.3.1002"]
                 [integrant/integrant "0.13.1"]
                 [integrant/repl  "0.4.0"]
                 [metosin/reitit-ring "0.8.0"]
                 [metosin/reitit-middleware "0.8.0"]
                 [org.clojure/clojure "1.12.0"]
                 [org.postgresql/postgresql "42.7.5"]
                 [ring/ring-core "1.14.1"]
                 [ring/ring-jetty-adapter "1.14.1"]
                 [ring/ring-json "0.5.1"]]
  :main ^:skip-aot app.core
  :plugins [[lein-ring "0.12.6"]]
  :target-path "target/%s"
  :ring {:handler app.handler/handler}
  :profiles {:uberjar
             {:aot      :all
              :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
