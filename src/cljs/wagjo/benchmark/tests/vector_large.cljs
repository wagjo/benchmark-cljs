;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.vector-large
  "Various benchmark for collections."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.data.ftree :as ft]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [cljs.core.rrb-vector :as rrb]
            [wagjo.data.array :as ua]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; create

(def large-size 1000)

(def ls (doall (take large-size (map str (repeatedly #(rand-int 100))))))

(defn ^:export t2-create-array
  []
  (to-array ls))

(defn ^:export t2-create-vector
  []
  (vec ls))

(defn ^:export t2-create-rrb-vector
  []
  (rrb/vec ls))

(defbenchmark create
  20 100 []
  "average overhead for this benchmark" (crunch nil)
  "array" (crunch (t2-create-array))
  "vector" (crunch (t2-create-vector))
  "rrb vector" (crunch (t2-create-rrb-vector))
  "Firefox has problems with large vector benchmarks. Hopefully it's the fault of benchmark code rather than Firefox being slow.")

;;; conj

(def t4-array (to-array ls))

(def t4-vector (vec ls))

(def t4-rrb-vector (rrb/vec ls))

(defbenchmark conj
  50 1000 []
  "average overhead for this benchmark" (crunch nil)
  "array" (crunch (ua/conjr t4-array 5))
  "vector" (crunch (conj t4-vector 5))
  "rrb vector" (crunch (conj t4-rrb-vector 5))
  "Arrays are not persistent, so conjoining is expensive.")

;;; access

(def t3-array (to-array ls))

(def t3-vector (vec ls))

(def t3-rrb-vector (rrb/vec ls))

(defbenchmark random-access
  50 10000 []
  "average overhead for this benchmark" (crunch (rand-int large-size))
  "array" (crunch (aget t3-array (rand-int large-size)))
  "vector" (crunch (nth t3-vector (rand-int large-size)))
  "rrb vector" (crunch (nth t3-rrb-vector (rand-int large-size)))
  "")

;;; reduce

(def t5-array (to-array ls))

(def t5-vector (vec ls))

(def t5-rrb-vector (rrb/vec ls))

(defbenchmark reduce
  30 200 []
  "average overhead for this benchmark" (crunch nil)
  "array" (crunch (ua/reduce + 0 t5-array))
  "vector" (crunch (reduce + 0 t5-vector))
  "rrb vector" (crunch (reduce + 0 t5-rrb-vector))
  "rrb vector reduce-kv" (crunch (reduce-kv #(+ % %3) 0 t5-rrb-vector))
  "")

;;; assoc

(def t6-array (to-array ls))

(def t6-vector (vec ls))

(def t6-rrb-vector (rrb/vec ls))

(defbenchmark assoc
  50 10000 []
  "average overhead for this benchmark" (crunch (rand-int large-size))
  "array" (crunch (ua/assoc t6-array (rand-int large-size) 5))
  "vector" (crunch (assoc t6-vector (rand-int large-size) 5))
  "rrb vector" (crunch (assoc t6-rrb-vector (rand-int large-size) 5))
  "Assoc is expensive in arrays too.")

;;; insert

(def t7-array (to-array ls))

(def t7-vector (vec ls))

(def t7-rrb-vector (rrb/vec ls))

(defbenchmark insert
  50 100 []
  "average overhead for this benchmark" (crunch (rand-int large-size))
  "array" (crunch (ua/insert-before t7-array (rand-int large-size) 5))
  "vector" (crunch (let [r (rand-int large-size)
                         b (subvec t7-vector 0 r)
                         a (subvec t7-vector r)]
                     (into (conj b 5) a)))
  "rrb vector" (crunch (let [r (rand-int large-size)
                             b (rrb/subvec t7-rrb-vector 0 r)
                             a (rrb/subvec t7-rrb-vector r)]
                         (rrb/catvec (conj b 5) a)))
  "Whole point of rrb vector is fast insert/remove.")

;;; remove

(def t8-array (to-array ls))

(def t8-vector (vec ls))

(def t8-rrb-vector (rrb/vec ls))

(defbenchmark remove
  50 100 []
  "average overhead for this benchmark" (crunch (rand-int large-size))
  "array" (crunch (ua/remove-at t8-array (rand-int large-size)))
  "vector" (crunch (let [r (rand-int (dec large-size))
                         b (subvec t8-vector 0 r)
                         a (subvec t8-vector (inc r))]
                     (into b a)))
  "rrb vector" (crunch (let [r (rand-int (dec large-size))
                             b (rrb/subvec t8-rrb-vector 0 r)
                             a (rrb/subvec t8-rrb-vector (inc r))]
                         (rrb/catvec b a)))
  "Whole point of rrb vector is fast insert/remove.")
