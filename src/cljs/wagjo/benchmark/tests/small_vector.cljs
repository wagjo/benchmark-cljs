;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.small-vector
  "ArrayVector tests for small vectors."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [cljs.core.rrb-vector :as rrb]))

;;;; Public API

;; ArrayVector is useful for passing multiple values from/to
;; functions. It is faster and has smaller footprint than
;; PersistentVector for small vectors (< 32). Moreover,
;; destructuring ArrayVectors is heavily optimized and much
;; more faster than any other collection.

;;; vector creation

(defn ^{:export "small_vectorCreateArray"} create-array
  "Returns new array."
  []
  (array 1 2.0 "b"))

(defn ^{:export "small_vectorCreateArrayVector"} create-array-vector
  "Returns new array vector."
  []
  (ArrayVector. nil (array 1 2.0 "b") nil))

(defn ^{:export "small_vectorCreatePersistentVector"}
  create-persistent-vector
  "Returns new persistent vector."
  []
  (cljs.core.PersistentVector/fromArray (array 1 2.0 "b") true))

(defbenchmark create-benchmark
  300 10000
  []
  "array" (create-array)
  "array vector" (create-array-vector)
  "persistent vector" (create-persistent-vector))

;;; vector access

(defn ^{:export "small_vectorAccessArray"} access-array
  "Accesses first three elements."
  [arr]
  (let [x (aget arr 0)
        y (aget arr 1)
        z (aget arr 2)]
    [x y z]))

(defn ^{:export "small_vectorAccessArrayVector"} access-array-vector
  "Accesses first three elements."
  [avec]
  (let [x (nth avec 0)
        y (nth avec 1)
        z (nth avec 2)]
    [x y z]))

(defn ^{:export "small_vectorAccessPersistentVector"}
  access-persistent-vector
  "Accesses first three elements."
  [vec]
  (let [x (nth vec 0)
        y (nth vec 1)
        z (nth vec 2)]
    [x y z]))

(defn ^{:export "small_vectorDestructureArrayVector"}
  destructure-array-vector
  "Accesses first three elements by destructuring."
  [avec]
  (let [[x y z] ^ArrayVector avec]
    [x y z]))

(defn ^{:export "small_vectorDestructurePersistentVector"}
  destructure-persistent-vector
  "Accesses first three elements by destructuring."
  [vec]
  (let [[x y z] vec]
    [x y z]))

(defbenchmark access-benchmark
  50 10000
  [arr (create-array)
   avec (create-array-vector)
   pvec (create-persistent-vector)]
  "array" (access-array arr)
  "array vector" (access-array-vector avec)
  "persistent vector" (access-persistent-vector pvec)
  "destructure array vector" (destructure-array-vector avec)
  "destructure persistent vector"
  (destructure-persistent-vector pvec))

;;; conjoining

(defn ^{:export "small_vectorConjArray"} conj-array
  "Conjoins to array."
  [arr]
  (let [new-arr (.slice arr)]
    (.push new-arr -3)
    new-arr))

(defn ^{:export "small_vectorConjArrayVector"} conj-array-vector
  "Conjoins to ArrayVector."
  [avec]
  (conj avec -3))

(defn ^{:export "small_vectorConjPersistentVector"}
  conj-persistent-vector
  "Conjoins to PersistentVector."
  [vec]
  (conj vec -3))

(defbenchmark conj-benchmark
  100 10000
  [arr (create-array)
   avec (create-array-vector)
   pvec (create-persistent-vector)]
  "array" (conj-array arr)
  "array vector" (conj-array-vector avec)
  "persistent vector" (conj-persistent-vector pvec))
