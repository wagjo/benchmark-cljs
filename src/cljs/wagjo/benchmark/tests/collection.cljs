;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.collection
  "Various benchmark for collections."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [cljs.core.rrb-vector :as rrb]))

;;;; Public API

;;; contains

(defn create-array
  "Returns new array."
  []
  (array 1 2.0 "b"))

(defn create-array-vector
  "Returns new array vector."
  []
  (ArrayVector. nil (array 1 2.0 "b") nil))

(defn create-persistent-vector
  "Returns new persistent vector."
  []
  (cljs.core.PersistentVector/fromArray (array 1 2.0 "b") true))

(defbenchmark small-contains?
  50 10000
  [s #{:a :b :c}
   v [:a :b :c]
   r (+ 5 (rand-int 5))
   sfn #(= r %)
   sfn2 #(== r %)]
  "set" (contains? s r)
  "vector with =" (some #(= r %) v)
  "vector with ==" (some #(== r %) v)
  "vector with = with predicate definition outside the loop"
  (some sfn v)
  "vector with == with predicate definition outside the loop"
  (some sfn2 v)
  "ad-hoc with =" (or (= r 1) (= r 2) (= r 3))
  "ad-hoc with ==" (or (== r 1) (== r 2) (== r 3))
  "Do not forget to use == for numbers, it is much more efficient.")

;;; 
