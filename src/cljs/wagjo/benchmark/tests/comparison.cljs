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

(defbenchmark small-collection
  20 1000 [m (create-map)
           r (create-record)
           av (create-array-vector)
           pv (create-persistent-vector)
           s (create-set)
           ls (create-seq)
           kvv (vec (seq m))
           m2 (create-map)
           r2 (create-record)
           av2 (create-array-vector)
           pv2 (create-persistent-vector)
           s2 (create-set)
           ls2 (create-seq)
           kvv2 (vec (seq m2))]
  "(= array-vector array-vector)" (= av av2)
  "(= persistent-vector persistent-vector)" (= pv pv2)
  "(= seq seq)" (= ls ls2)
  "(= set set)" (= s s2)
  "(= vector-of-keyvals vector-of-keyvals)" (= kvv kvv2)
  "(= map map)" (= m m2)
  "(= record record)" (= r r2)
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

(defbenchmark contains?
  100 1000
  [v [:a :b :c]
   s (set v)
   r (rand-nth [:d :e])
   rb (nil? r)]
  "average overhead for this benchmark"
  (when rb (crunch nil))
  "(or (identical? x val1) (identical? x val2) (identical? x val3))"
  (when (or (identical? r :a) (identical? r :b) (identical? r :c)) (crunch nil))
  "(contains? set x)"
  (when (contains? s r) (crunch nil))
  "(some #(identical? x %) vector)"
  (when (some #(identical? r %) v) (crunch nil))
  "(or (= x val1) (= x val2) (= x val3))"
  (when (or (= r :a) (= r :b) (= r :c)) (crunch nil))
  "(some #(= x %) vector)"
  (when (some #(= r %) v) (crunch nil))
  "Use ad-hoc testing for small set of values you know beforehand, use contains? for all other cases. Use identical? when you can (e.g. for strings, keywords, symbols).")
