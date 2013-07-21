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
