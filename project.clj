(defproject com.wagjo/benchmark-cljs "0.1.0-SNAPSHOT"
  :description "Benchmarking ClojureScript stuff."
  :url "https://github.com/wagjo/benchmark-cljs"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.2"] ;; needed for piggieback
                 [core.async "0.1.0-SNAPSHOT"]
                 [com.wagjo/clojurescript "0.1.0-SNAPSHOT"]
                 [com.wagjo/data-cljs "0.1.0-SNAPSHOT"]
                 [org.clojure/core.rrb-vector "0.0.10-SNAPSHOT"]
                 [com.wagjo/tools-cljs "0.1.0-SNAPSHOT"]
                 [com.cemerick/piggieback "0.0.5"
                   :exclusions [org.clojure/clojurescript
                                org.clojure/clojure]]]
  :source-paths ["src/cljs"]
  :global-vars {*warn-on-reflection* true}
  :repl-options {:port 41817
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as ClojureScript"})
