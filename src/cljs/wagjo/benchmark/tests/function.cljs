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
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "calling (defn f [x1 x2 x3] ...)" (arg-fixed nil nil nil)
  "calling (defn f [& xs] ...)" (arg-var nil nil nil)
  "calling (defn f ([& xs] ...))" (arg-var-other nil nil nil)
  "calling (defn f ([] ...) ([& xs] ...))" (arg-var-arity nil nil nil)
  "Calling variadic function is slower.")

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
  200 10000 []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "calling a function" (arity-1 nil)
  "calling a function overloaded on arity (2 different arities)" (arity-2 nil)
  "calling a function overloaded on arity (5 different arities)" (arity-5 nil)
  "Calling a function which is overloaded on arity is slightly slower in Chrome, but makes no difference in Firefox.")

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
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "calling (fn [y] (f x y)) created outside the loop" (fn-apply nil)
  "calling #(f x %) created outside the loop" (f2-apply nil)
  "calling #(f x %) created inside the loop" (let [f1 #(pfn nil %)] (f1 nil))
  "calling (partial f x) created outside the loop" (partial-apply nil)
  "calling (partial f x) created inside the loop"
  (let [pf (partial pfn nil)] (pf nil))
  "Partial is very slow. Also do note that creating anonymous function costs something. Try not to create anonymous functions inside performance sensitive loops, as they are recreated in each iteration.")

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

(def a (rand-int 10))
(def b (rand-int 10))
(def c (rand-int 10))
(def d (rand-int 10))
(def e (rand-int 10))
(def arr (array a b c d e))
(def pvec (cljs.core.PersistentVector/fromArray arr true))
(def iseq (doall (seq pvec)))
(def avec (ArrayVector. nil arr nil))
(def mapx {:a a :b b :c c :d d :e e})
(def ct (Passer. a b c d e))

(defbenchmark passing-values
  50 10000
  []
  "average overhead for this benchmark" (do (crunch nil) (crunch (+ a b c d e)))
  "input to arguments (N/A for returning multiple values)" (passme-classic a b c d e)
  "pass custom type" (passme-type ct)
  "pass array" (passme-array arr)
  "pass array vector" (passme-avec avec)
  "pass seq" (passme-pvec iseq)
  "pass ordinary vector" (passme-pvec pvec)
  "input to variadic function (N/A for returning multiple values)" (passme-var a b c d e)
  "input to variadic function through apply seq (N/A for returning multiple values)"
  (apply passme-var iseq)
  "input to variadic function through apply vec (N/A for returning multiple values)"
  (apply passme-var pvec)
  "pass ordinary map" (passme-map mapx)
  "pass ordinary map without destructuring" (passme-map-wod mapx)
  "If you need to pass fixed number of multiple values, try to avoid maps, seqs and variadic functions. But most of time you don't really have a choice, because often the data is already in some kind of a collection and it would be inefficient to convert it to something else.")
