;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.core
  "Main entry point.")

;;;; Implementation details

;;;; Public API

(defn ^{:export "benchmarkInit"} init!
  "Initializes benchmarking.
  Is called after page loading is complete."
  []
  (.log js/console "foo bar"))
