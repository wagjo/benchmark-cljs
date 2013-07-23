;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.comparison
  "Comparison."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; compare numbers

(defbenchmark number
  200 10000 [n (rand-int 10)
           n2 (rand-int 10)]
  "(== num num)" (== n n2)
  "(= num num)" (= n n2)
  "When comparing numbers, prefer == to the = .")

;;; creation

(defn- create-map []
  {:a 1
   :b 2.0
   :c "3"
   :d -4
   :e :five})

(defrecord MyRecord [a b c d e])
(defn- create-my-record [a b c d e] (MyRecord. a b c d e))
(defn- create-record []
  (create-my-record 1 2.0 "3" -4 :five))

(defn create-array
  "Returns new array."
  []
  (array 1 2.0 "3" -4 :five))

(defn create-array-vector
  "Returns new array vector."
  []
  (ArrayVector. nil (array 1 2.0 "3" -4 :five) nil))

(defn create-persistent-vector
  "Returns new persistent vector."
  []
  (cljs.core.PersistentVector/fromArray (array 1 2.0 "3" -4 :five)
                                        true))

(defn- create-set []
  #{1 2.0 "3" -4 :five})

(defn- create-seq []
  (doall (map identity (create-array-vector))))

(def xm (create-map))
(def xr (create-record))
(def xav (create-array-vector))
(def xpv (create-persistent-vector))
(def xs (create-set))
(def xls (create-seq))
(def xkvv (vec (seq xm)))
(def xm2 (create-map))
(def xr2 (create-record))
(def xav2 (create-array-vector))
(def xpv2 (create-persistent-vector))
(def xs2 (create-set))
(def xls2 (create-seq))
(def xkvv2 (vec (seq xm2)))

(defbenchmark small-collection
  20 1000 []
  "(= array-vector array-vector)" (= xav xav2)
  "(= persistent-vector persistent-vector)" (= xpv xpv2)
  "(= seq seq)" (= xls xls2)
  "(= set set)" (= xs xs2)
  "(= vector-of-keyvals vector-of-keyvals)" (= xkvv xkvv2)
  "(= map map)" (= xm xm2)
  "(= record record)" (= xr xr2)
  "When comparing small collections for equivalence, vectors are fastest to compare.")

;;; contains?

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

(def v [:a :b :c])
(def s (set v))
(def r (rand-nth [:d :e]))
(def rb (nil? r))

(defbenchmark contains?
  100 1000
  []
  "average overhead for this benchmark"
  (when rb (crunch nil))
  "ad-hoc (or (identical? x val1) (identical? x val2) (identical? x val3))"
  (when (or (identical? r :a) (identical? r :b) (identical? r :c)) (crunch nil))
  "ad-hoc with many tests"
  (when (or (identical? r :a) (identical? r :b) (identical? r :c)
            (identical? r :a1) (identical? r :b1) (identical? r :c1)
            (identical? r :a2) (identical? r :b2) (identical? r :c2)
            (identical? r :a3) (identical? r :b3) (identical? r :c3)
            (identical? r :a4) (identical? r :b4) (identical? r :c4)) (crunch nil))
  "(contains? set x)"
  (when (contains? s r) (crunch nil))
  "(some #(identical? x %) vector)"
  (when (some #(identical? r %) v) (crunch nil))
  "ad-hoc (or (= x val1) (= x val2) (= x val3))"
  (when (or (= r :a) (= r :b) (= r :c)) (crunch nil))
  "(some #(= x %) vector)"
  (when (some #(= r %) v) (crunch nil))
  "Use ad-hoc testing for set of values you know beforehand, use contains? for all other cases. Use identical? when you can (e.g. for strings, keywords, symbols).")
