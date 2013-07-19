;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.core
  "Main entry point."
  (:require-macros [wagjo.tools.log :as log]
                   [wagjo.tools.profile :as prof])
  (:require [wagjo.tools.profile :as prof]
            [cljs.core.rrb-vector :as rrb]
            [wagjo.benchmark.state :as state]
            [wagjo.benchmark.page :as wp]
            [wagjo.benchmark.list]))

;;;; Implementation details

;;;; Public API

(defn ^{:export "benchmarkInit"} init!
  "Initializes benchmarking.
  Is called after page loading is complete."
  []
  )

(defn ^{:export "benchmarkRun"} run!
  "Runs all available benchmarks."
  []
  (let [benchmarks @state/benchmarks-ref
        sorted (sort-by :name benchmarks)]
    ;; TODO: non-blocking run
    (doseq [b sorted]
      (apply wp/dom-print-benchmark ((:fn b)))))
  (wp/wait-over!))
