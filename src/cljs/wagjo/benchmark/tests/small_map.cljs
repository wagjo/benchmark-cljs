;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.small-map
  "Small persistent maps vs custom types"
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

(deftype MyType [a b c d e])
(defrecord MyRecord [a b c d e])
(defn- create-my-type [a b c d e] (MyType. a b c d e))
(defn- create-my-record [a b c d e] (MyRecord. a b c d e))
(defn- get-a [o] (.-a o))
(defn- get-b [o] (.-b o))
(defn- get-c [o] (.-c o))
(defn- get-d [o] (.-d o))
(defn- get-e [o] (.-e o))

(defn- create-type []
  (create-my-type 1 2.0 "3" -4 :five))

(defn- create-record []
  (create-my-record 1 2.0 "3" -4 :five))

(defbenchmark create
  50 10000 []
  "map" (create-map)
  "custom type" (create-type)
  "custom record" (create-record)
  "Creating type/record instance is faster.")

;;; access

(defn- access-map-get [m]
  (let [a (get m :a)
        b (get m :b)
        c (get m :c)
        d (get m :d)
        e (get m :e)]
    [a b c d e]))

(defn- access-map-keyword [m]
  (let [a (:a m)
        b (:b m)
        c (:c m)
        d (:d m)
        e (:e m)]
    [a b c d e]))

(defn- access-map-map [m]
  (let [a (m :a)
        b (m :b)
        c (m :c)
        d (m :d)
        e (m :e)]
    [a b c d e]))

(defn- access-type [o]
  (let [a (get-a o)
        b (get-b o)
        c (get-c o)
        d (get-d o)
        e (get-e o)]
    [a b c d e]))

(defbenchmark access
  50 10000
  [m (create-map)
   t (create-type)
   r (create-record)]
  "(get m :a)" (access-map-get m)
  "(:a m)" (access-map-keyword m)
  "(m :a)" (access-map-map m)
  "(.-a m)" (access-type t)
  "(get r :a)" (access-map-get r)
  "(:a r)" (access-map-keyword r)
  "(.-a r)" (access-type r)
  "Accessing type/record field directly is fastest. Accessing by keyword first is slowest.")

;;; assoc

(defn- assoc-map [m]
  (assoc m :c 8))

(defn- assoc-type [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (create-my-type a b 8 d e)))

(defn- assoc-record [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (create-my-record a b 8 d e)))

(defbenchmark assoc
  50 10000
  [m (create-map)
   t (create-type)
   r (create-record)]
  "map assoc" (assoc-map m)
  "type clone" (assoc-type t)
  "record assoc" (assoc-map r)
  "record clone" (assoc-record r)
  "Assoc by cloning is faster.")
