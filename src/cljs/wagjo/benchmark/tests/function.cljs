;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.function
  "Function calling."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; variadic

(defn- ^:export arg-fixed [a b c] (crunch nil) (crunch nil))
(defn- ^:export arg-var [& xs] (crunch nil) (crunch nil))
(defn- ^:export arg-var-other ([& xs] (crunch nil) (crunch nil)))
(defn- ^:export arg-var-arity
  ([] (crunch nil) (crunch nil))
  ([& xs] (crunch nil) (crunch nil)))

(defbenchmark variadic
  50 10000 []
  "baseline" (do (crunch nil) (crunch nil))
  "fixed arguments" (arg-fixed nil nil nil)
  "variadic argument" (arg-var nil nil nil)
  "variadic argument other notation" (arg-var-other nil nil nil)
  "variadic in multi arity" (arg-var-arity nil nil nil)
  "Variadics are slower.")

;;; arities

(defn- ^:export arity-1 ([x] (crunch nil) (crunch nil)))

(defn- ^:export arity-2
  ([]
     (crunch nil) (crunch nil))
  ([x]
     (crunch nil) (crunch nil)))

(defn- ^:export arity-5
  ([]
     (crunch nil) (crunch nil))
  ([x]
     (crunch nil) (crunch nil))
  ([x y]
     (crunch nil) (crunch nil))
  ([x y z]
     (crunch nil) (crunch nil))
  ([x y z a]
     (crunch nil) (crunch nil)))

(defbenchmark arities
  50 10000 []
  "baseline" (do (crunch nil) (crunch nil))
  "no multiple arities" (arity-1 nil)
  "2 different arities" (arity-2 nil)
  "5 different arities" (arity-5 nil)
  "Slight overhead in Chrome, no difference in Firefox.")

;;; partials

(defn- ^:export pfn
  [a b]
  (crunch a) (crunch b))

(def ^:export partial-apply
  (partial pfn nil))

(def ^:export fn-apply
  (fn [x] (pfn nil x)))

(def ^:export f2-apply
  #(pfn nil %))

(defbenchmark partials
  50 10000 []
  "baseline" (do (crunch nil) (crunch nil))
  "(partial f x) created outside the loop" (partial-apply nil)
  "(partial f x) created inside the loop"
  (let [pf (partial pfn nil)] (pf nil))
  "#(f x %) created outside the loop" (f2-apply nil)
  "#(f x %) created inside the loop" (let [f1 #(pfn nil %)] (f1 nil))
  "(fn [y] (f x y)) created outside the loop" (fn-apply nil)
  "Partial is very slow. Also do note that creating anonymous function costs something. This was not much an issue in the Clojure. Try not to create anonymous function inside performance sensitive loops, as it is created in each iteration.")

;;; passing a value

(defn- ^:export passme-classic
  [a b c d e]
  (crunch nil) (crunch (+ a b c d e)))

(defn- ^:export passme-array
  [arr]
  (let [a (aget arr 0)
        b (aget arr 1)
        c (aget arr 2)
        d (aget arr 3)
        e (aget arr 4)]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-pvec
  [pvec]
  (let [[a b c d e] pvec]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-avec
  [avec]
  (let [[a b c d e] ^ArrayVector avec]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-var
  [& xs]
  (let [[a b c d e] xs]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-map
  [map]
  (let [{:keys [a b c d e]} map]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-map-wod
  [map]
  (let [a (get map :a)
        b (get map :b)
        c (get map :c)
        d (get map :d)
        e (get map :e)]
    (crunch nil) (crunch (+ a b c d e))))

(defn- ^:export passme-type
  [t]
  (let [a (.-a t)
        b (.-b t)
        c (.-c t)
        d (.-d t)
        e (.-e t)]
    (crunch nil) (crunch (+ a b c d e))))

(deftype Passer [a b c d e])

(defbenchmark passing-values
  50 10000
  [a (rand-int 10)
   b (rand-int 10)
   c (rand-int 10)
   d (rand-int 10)
   e (rand-int 10)
   arr (array a b c d e)
   pvec (cljs.core.PersistentVector/fromArray arr true)
   iseq (doall (seq pvec))
   avec (ArrayVector. nil arr nil)
   map {:a a :b b :c c :d d :e e}
   ct (Passer. a b c d e)]
  "baseline" (do (crunch nil) (crunch (+ a b c d e)))
  "input in arguments" (passme-classic a b c d e)
  "input in array" (passme-array arr)
  "input in seq" (passme-pvec iseq)
  "input in ordinary vector" (passme-pvec pvec)
  "input in array vector" (passme-avec avec)
  "input to variadic function" (passme-var a b c d e)
  "input to variadic function through apply seq"
  (apply passme-var iseq)
  "input to variadic function through apply vec"
  (apply passme-var pvec)
  "input in ordinary map without destructuring" (passme-map-wod map)
  "input in ordinary map" (passme-map map)
  "pass custom type" (passme-type ct)
  "Ordinary collections performs badly. This adds up if you often pass multiple values to/from function in the loop.")
