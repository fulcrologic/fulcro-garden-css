(defproject com.fulcrologic/fulcro-garden-css "3.0.1"
  :description "Add co-located CSS to Fulcro components"
  :url "https://github.com/fulcrologic/fulcro-garden-css"
  :lein-min-version "2.8.1"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}

  :source-paths ["src/main"]
  :resource-paths ["resources"]
  :test-paths ["src/test"]

  :plugins [[lein-tools-deps "0.4.1"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]
                           :aliases [:provided]})
