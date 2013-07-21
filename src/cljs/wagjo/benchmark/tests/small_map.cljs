;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.small-map
  "Small persistent maps vs custom types"
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; creation

(defn- ^:export create-map []
  {:a 1
   :b 2.0
   :c "3"
   :d (rand-int 10)
   :e :five})

(deftype MyType [a b c d e])
(defrecord MyRecord [a b c d e])
(defn- ^:export create-my-type [a b c d e] (MyType. a b c d e))
(defn- ^:export create-my-record [a b c d e] (MyRecord. a b c d e))
(defn- ^:export get-a [o] (.-a o))
(defn- ^:export get-b [o] (.-b o))
(defn- ^:export get-c [o] (.-c o))
(defn- ^:export get-d [o] (.-d o))
(defn- ^:export get-e [o] (.-e o))

(defn- ^:export create-type []
  (create-my-type 1 2.0 "3" (rand-int 10) :five))

(defn- ^:export create-record []
  (create-my-record 1 2.0 "3" (rand-int 10) :five))

(defbenchmark create
  50 10000 []
  "baseline" (do (crunch nil) (crunch (rand-int 10)))
  "map" (do (crunch nil) (crunch (create-map)))
  "custom type" (do (crunch nil) (crunch (create-type)))
  "custom record" (do (crunch nil) (crunch (create-record)))
  "Creating type/record instance is faster.")

;;; access

(defn- ^:export access-map-get [m]
  (let [a (get m :a)
        b (get m :b)
        c (get m :c)
        d (get m :d)
        e (get m :e)]
    (crunch nil) (crunch [a b c d e])))

(defn- ^:export access-map-keyword [m]
  (let [a (:a m)
        b (:b m)
        c (:c m)
        d (:d m)
        e (:e m)]
    (crunch nil) (crunch [a b c d e])))

(defn- ^:export access-map-map [m]
  (let [a (m :a)
        b (m :b)
        c (m :c)
        d (m :d)
        e (m :e)]
    (crunch nil) (crunch [a b c d e])))

(defn- ^:export access-type [o]
  (let [a (get-a o)
        b (get-b o)
        c (get-c o)
        d (get-d o)
        e (get-e o)]
    (crunch nil) (crunch [a b c d e])))

(defbenchmark access
  50 10000
  [m (create-map)
   t (create-type)
   r (create-record)
   a (rand-int 10)]
  "baseline" (do (crunch nil) (crunch [a 2 3 4 5]))
  "(get m :a)" (access-map-get m)
  "(:a m)" (access-map-keyword m)
  "(m :a)" (access-map-map m)
  "(.-a m)" (access-type t)
  "(get r :a)" (access-map-get r)
  "(:a r)" (access-map-keyword r)
  "(.-a r)" (access-type r)
  "Accessing type/record field directly is fastest. Accessing by keyword first is slowest.")

;;; assoc

(defn- ^:export assoc-map [m]
  (crunch nil) (crunch (assoc m :c 8)))

(defn- ^:export assoc-type [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-type a b 8 d e))))

(defn- ^:export assoc-record [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-record a b 8 d e))))

(defbenchmark assoc
  50 10000
  [m (create-map)
   t (create-type)
   r (create-record)]
  "baseline" (do (crunch nil) (crunch nil))
  "map assoc" (assoc-map m)
  "type clone" (assoc-type t)
  "record assoc" (assoc-map r)
  "record clone" (assoc-record r)
  "Assoc by cloning is faster.")
