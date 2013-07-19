;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.core
  "Main entry point."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! close! chan timeout]]
            [cljs.core.rrb-vector :as rrb]
            [wagjo.benchmark.state :as state]
            [wagjo.benchmark.page :as wp]
            [wagjo.benchmark.list]))

;;;; Implementation details

;;;; Public API

(defn ^{:export "benchmarkInit"} init!
  "Initializes benchmarking.
  Is called after page loading is complete."
  [])

(defn ^{:export "benchmarkInitPage"} init-page!
  "Initializes benchmarking.
  Is called after page loading is complete."
  []
  (wp/populate-menu @state/benchmarks-ref))

(defn run-tests
  [c]
  (go
   (wp/run-started!)
   (<! (timeout 10)) ;; allow page to repaint
   (loop [b (<! c)]
     (if (nil? b)
       (wp/wait-over!)
       (do
         (apply wp/dom-print-benchmark ((:fn b)))
         (recur (<! c)))))))

(defn ^{:export "benchmarkRun"} run!
  "Runs all available benchmarks."
  []
  (let [c (chan)]
    (go
     (let [benchmarks @state/benchmarks-ref
           sorted (sort-by :path benchmarks)]
       (doseq [b sorted]
         (>! c b))
       (close! c)))
    (run-tests c)))
