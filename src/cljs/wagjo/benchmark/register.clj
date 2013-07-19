;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.register
  "Benchmark registrar.")

;;;; Public API

(defmacro defbenchmark
  "Defines and registers benchmark."
  [name & body]
  (let [name-str (str *ns* "/" name)]
    `(do
       (defn ~name []
         (wagjo.tools.profile/benchmark ~(subs name-str 22) ~@body))
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
