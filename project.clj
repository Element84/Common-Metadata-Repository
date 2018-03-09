(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      ;(slurp "resources/text/loading.txt")
      )
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject cmr-graph "0.1.0-SNAPSHOT"
  :description "A service and API for querying CMR metadata relationships"
  :url "https://github.com/cmr-exchange/cmr-graph"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojurewerkz/neocons "3.2.0"]
    [com.stuartsierra/component "0.3.2"]
    [metosin/reitit "0.1.0"]
    [org.clojure/clojure "1.9.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns cmr.graph.dev
        :prompt ~get-prompt
        :init ~(println (get-banner))}
      :plugins [
        [lein-shell "0.5.0"]]}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.2.5"]
        [lein-ancient "0.6.15"]
        [lein-bikeshed "0.5.1"]
        [lein-kibit "0.1.6"]
        [venantius/yagni "0.1.4"]]}
    :test {
      :dependencies [
        [clojusc/ltest "0.3.0"]]
      :plugins [
        [lein-ltest "0.3.0"]]
      :test-selectors {
        :select :select}}}
  :aliases {
    ;; Dev Aliases
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ;["eastwood"]
      ]
    "ltest" ["with-profile" "+test" "ltest"]
    ;; Container Aliases
    "start-infra"
      ["shell"
       "docker-compose" "-f" "resources/docker/docker-compose.yml" "up"]
    "stop-infra" [
      "shell"
      "docker-compose" "-f" "resources/docker/docker-compose.yml" "down"]
    "neo4j-bash" [
      "shell"
      "docker" "exec" "-it" "cmr-graph-neo4j" "bash"]
    "elastic-bash" [
      "shell"
      "docker" "exec" "-it" "cmr-graph-elastic" "bash"]
    "kibana-bash" [
      "shell"
      "docker" "exec" "-it" "cmr-graph-kibana" "bash"]})
