;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.vector
  "ArrayVector tests."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [cljs.core.rrb-vector :as rrb]))

;;;; Public API

;;; vector creation

(defn ^{:export "vectorCreateArray"} create-array
  "Returns new array."
  []
  (array 1 2.0 "b"))

(defn ^{:export "vectorCreateArrayVector"} create-array-vector
  "Returns new array vector."
  []
  (ArrayVector. nil (array 1 2.0 "b") nil))

(defn ^{:export "vectorCreatePersistentVector"}
  create-persistent-vector
  "Returns new persistent vector."
  []
  (cljs.core.PersistentVector/fromArray (array 1 2.0 "b") true))

(defbenchmark create-benchmark
  500 10000
  []
  "array" (create-array)
  "array vector" (create-array-vector)
  "persistent vector" (create-persistent-vector))
