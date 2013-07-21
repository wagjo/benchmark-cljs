;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.function
  "Function calling."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]))

;;; arguments

(def a 1)
(def b 2)
(def c 3)
(def d 4)
(def e 5)
(def f 6)
(def g 7)
(def h 8)
(def i 9)
(def j 10)

(defn- arg-0 []
  [a b c d e f g h i j])
(defn- arg-1 [la]
  [la b c d e f g h i j])
(defn- arg-2 [la lb]
  [la lb c d e f g h i j])
(defn- arg-3 [la lb lc]
  [la lb lc d e f g h i j])
(defn- arg-4 [la lb lc ld]
  [la lb lc ld e f g h i j])
(defn- arg-5 [la lb lc ld le]
  [la lb lc ld le f g h i j])
(defn- arg-6 [la lb lc ld le lf]
  [la lb lc ld le lf g h i j])
(defn- arg-7 [la lb lc ld le lf lg]
  [la lb lc ld le lf lg h i j])
(defn- arg-8 [la lb lc ld le lf lg lh]
  [la lb lc ld le lf lg lh i j])
(defn- arg-9 [la lb lc ld le lf lg lh li]
  [la lb lc ld le lf lg lh li j])
(defn- arg-10 [la lb lc ld le lf lg lh li lj]
  [la lb lc ld le lf lg lh li lj])

(defbenchmark arguments
  50 10000 [la 1
            lb 2
            lc 3
            ld 4
            le 5
            lf 6
            lg 7
            lh 8
            li 9
            lj 10]
  "direct computation" [la lb lc ld le lf lg lh li lj]
  "no arguments" (arg-0)
  "1 argument" (arg-1 la)
  "2 arguments" (arg-2 la lb)
  "9 arguments" (arg-9 la lb lc ld le lf lg lh li)
  "10 arguments" (arg-10 la lb lc ld le lf lg lh li lj)
  "Number of arguments makes no difference.")

;;; arities

(defn- arity-1
  ([la] [la b c d e f g h i j]))

(defn- arity-2
  ([] [a b c d e f g h i j])
  ([la] [la b c d e f g h i j]))

(defn- arity-5
  ([] [a b c d e f g h i j])
  ([la] [la b c d e f g h i j])
  ([la lb] [la lb c d e f g h i j])
  ([la lb lc] [la lb lc d e f g h i j])
  ([la lb lc ld] [la lb lc ld e f g h i j]))

(defn- arity-10
  ([] [a b c d e f g h i j])
  ([la] [la b c d e f g h i j])
  ([la lb] [la lb c d e f g h i j])
  ([la lb lc] [la lb lc d e f g h i j])
  ([la lb lc ld] [la lb lc ld e f g h i j])
  ([la lb lc ld le] [la lb lc ld le f g h i j])
  ([la lb lc ld le lf] [la lb lc ld le lf g h i j])
  ([la lb lc ld le lf lg] [la lb lc ld le lf lg h i j])
  ([la lb lc ld le lf lg lh] [la lb lc ld le lf lg lh i j])
  ([la lb lc ld le lf lg lh li] [la lb lc ld le lf lg lh li j]))

(defbenchmark arities
  50 10000 [la 1]
  "normal function" (arg-1 la)
  "normal function with multi arity syntax" (arity-1 la)
  "2 different arities" (arity-2 la)
  "5 different arities" (arity-5 la)
  "10 different arities" (arity-10 la)
  "Multi arity function are slower, with similar overhead no matter how many different arity types they have.")

;;; partials

(defn- partial-apply
  [lb]
  (let [f (partial arg-2 a)]
    (f lb)))

(defn- fn-apply
  [lb]
  (let [f (fn [lb] (arg-2 a lb))]
    (f lb)))

(defn- reader-fn-apply
  [lb]
  (let [f #(arg-2 a %)]
    (f lb)))

(defbenchmark partials
  50 10000 [lb 2]
  "(partial f x)" (partial-apply lb)
  "(fn [y] (f x y))" (fn-apply lb)
  "#(f x %)" (reader-fn-apply lb)
  "Partial is freakingly slow.")
