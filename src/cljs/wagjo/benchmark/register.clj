;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.register
  "Benchmark registrar."
  (:require [clojure.string :as cs]))

;;;; Public API

(defmacro defbenchmark
  "Defines and registers benchmark."
  [name & body]
  (let [name-str (str *ns* "/" name)
        label (str "<a href=\"https://github.com/wagjo/"
                   "benchmark-cljs/blob/master/src/cljs/"
                   (cs/replace (cs/replace *ns* #"\." "/")
                               #"\-" "_") ".cljs\">"
                   (subs name-str 22) "</a>")]
    `(do
       (defn ~name []
         (wagjo.tools.profile/benchmark ~label ~@body))
       (swap! wagjo.benchmark.state/benchmarks-ref
              conj
              {:name ~name-str :fn ~name}))))

(comment

  (macroexpand-1 '(defbenchmark create-benchmark
                    500 10000
                    []
                    "array" (create-array)
                    "array vector" (create-array-vector)
                    "persistent vector" (create-persistent-vector)))
  
)
