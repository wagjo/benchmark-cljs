;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.small-vector
  "ArrayVector tests for small vectors."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

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

(defbenchmark create
  300 10000
  []
  "baseline" (do (crunch nil) (crunch nil))
  "array" (do (crunch nil) (crunch (create-array)))
  "array vector" (do (crunch nil) (crunch (create-array-vector)))
  "persistent vector" (do (crunch nil)
                          (crunch (create-persistent-vector)))
  "Creating persistent vector instance is slowest.")

;;; vector access

(defn ^{:export "small_vectorAccessArray"} access-array
  "Accesses first three elements."
  [arr]
  (let [x (aget arr 0)
        y (aget arr 1)
        z (aget arr 2)]
    (crunch x) (crunch y) (crunch z)))

(defn ^{:export "small_vectorAccessArrayVector"} access-array-vector
  "Accesses first three elements."
  [avec]
  (let [x (nth avec 0)
        y (nth avec 1)
        z (nth avec 2)]
    (crunch x) (crunch y) (crunch z)))

(defn ^{:export "small_vectorAccessPersistentVector"}
  access-persistent-vector
  "Accesses first three elements."
  [vec]
  (let [x (nth vec 0)
        y (nth vec 1)
        z (nth vec 2)]
    (crunch x) (crunch y) (crunch z)))

(defn ^{:export "small_vectorDestructureArrayVector"}
  destructure-array-vector
  "Accesses first three elements by destructuring."
  [avec]
  (let [[x y z] ^ArrayVector avec]
    (crunch x) (crunch y) (crunch z)))

(defn ^{:export "small_vectorDestructurePersistentVector"}
  destructure-persistent-vector
  "Accesses first three elements by destructuring."
  [vec]
  (let [[x y z] vec]
    (crunch x) (crunch y) (crunch z)))

(defbenchmark access
  50 10000
  [arr (create-array)
   avec (create-array-vector)
   pvec (create-persistent-vector)]
  "baseline" (do (crunch nil) (crunch nil) (crunch nil))
  "array" (access-array arr)
  "array vector" (access-array-vector avec)
  "persistent vector" (access-persistent-vector pvec)
  "destructure array vector" (destructure-array-vector avec)
  "destructure persistent vector"
  (destructure-persistent-vector pvec)
  "Array vector shines when using destructuring and also slightly outperforms persistent vector in normal access too.")

;;; conjoining

(defn ^{:export "small_vectorConjArray"} conj-array
  "Conjoins to array."
  [arr]
  (crunch nil)
  (let [new-arr (.slice arr)]
    (.push new-arr -3)
    (crunch new-arr)))

(defn ^{:export "small_vectorConjArrayVector"} conj-array-vector
  "Conjoins to ArrayVector."
  [avec]
  (crunch nil) (crunch (conj avec -3)))

(defn ^{:export "small_vectorConjPersistentVector"}
  conj-persistent-vector
  "Conjoins to PersistentVector."
  [vec]
  (crunch nil) (crunch (conj vec -3)))

(defbenchmark conj
  100 10000
  [arr (create-array)
   avec (create-array-vector)
   pvec (create-persistent-vector)]
  "baseline" (do (crunch nil) (crunch nil))
  "array" (conj-array arr)
  "array vector" (conj-array-vector avec)
  "persistent vector" (conj-persistent-vector pvec)
  "Array is fastest, vectors performs the same.")
