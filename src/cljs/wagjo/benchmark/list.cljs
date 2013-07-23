;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.list
  "List of all benchmarks.
  Put all namespaces from wagjo.benchmark.tests in following require."
  (:require [wagjo.benchmark.tests.vector-small]
            [wagjo.benchmark.tests.map-small]
            [wagjo.benchmark.tests.function]
            [wagjo.benchmark.tests.polymorphism]
            [wagjo.benchmark.tests.comparison]
            [wagjo.benchmark.tests.mutation]
            [wagjo.benchmark.tests.string]
            [wagjo.benchmark.tests.vector-large]
            ))

;; Do not delete this file
