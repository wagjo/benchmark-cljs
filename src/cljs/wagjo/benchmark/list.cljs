;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.list
  "List of all benchmarks.
  Put all namespaces from wagjo.benchmark.tests in following require."
  (:require [wagjo.benchmark.tests.small-vector]
            [wagjo.benchmark.tests.small-map]
            [wagjo.benchmark.tests.function]
            [wagjo.benchmark.tests.polymorphism]
            [wagjo.benchmark.tests.comparison]
            [wagjo.benchmark.tests.mutation]
            [wagjo.benchmark.tests.string]
            ))

;; Do not delete this file
