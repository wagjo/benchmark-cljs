;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.equivalence
  "Comparing objects"
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]))

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

(defn ^{:export "small_vectorCreateArray"} create-array
  "Returns new array."
  []
  (array 1 2.0 "3" -4 :five))

(defn ^{:export "small_vectorCreateArrayVector"} create-array-vector
  "Returns new array vector."
  []
  (ArrayVector. nil (array 1 2.0 "3" -4 :five) nil))

(defn ^{:export "small_vectorCreatePersistentVector"}
  create-persistent-vector
  "Returns new persistent vector."
  []
  (cljs.core.PersistentVector/fromArray (array 1 2.0 "3" -4 :five)
                                        true))

(defn- create-set []
  #{1 2.0 "3" -4 :five})

(defn- create-lazy-seq []
  (doall (map identity (create-array-vector))))

(defn- create-object []
  (js-obj :a 1
          :b 2.0
          :c "3"
          :d -4
          :e :five))

(defbenchmark number
  50 10000 [n 10
           n2 10
           st "foo bar"
           st2 "foo bar"]
  "(== num num)" (== n n2)
  "(= num num)" (= n n2)
  "== is faster.")

(defbenchmark collection
  50 1000 [m (create-map)
           r (create-record)
           av (create-array-vector)
           pv (create-persistent-vector)
           s (create-set)
           ls (create-lazy-seq)
           m2 (create-map)
           r2 (create-record)
           av2 (create-array-vector)
           pv2 (create-persistent-vector)
           s2 (create-set)
           ls2 (create-lazy-seq)]
  "(= map map)" (= m m2)
  "(= record record)" (= r r2)
  "(= array-vector array-vector)" (= av av2)
  "(= persistent-vector persistent-vector)" (= pv pv2)
  "(= set set)" (= s s2)
  "(= lazy-seq lazy-seq)" (= ls ls2)
  "vectors are fastest.")
