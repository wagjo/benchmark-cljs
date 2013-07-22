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
  "average overhead for this benchmark" (do (crunch nil) (crunch (rand-int 10)))
  "(CustomType. x y z)" (do (crunch nil) (crunch (create-type)))
  "{:x x :y y :z z}" (do (crunch nil) (crunch (create-map)))
  "(CustomRecord. x y z)" (do (crunch nil) (crunch (create-record)))
  "Creating type instance is moderately faster than creating ordinary map or record instance.")

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
  "average overhead for this benchmark" (do (crunch nil) (crunch [a 2 3 4 5]))
  "(.-a type)" (access-type t)
  "(.-a record)" (access-type r)
  "(get record :a)" (access-map-get r)
  "(:a record)" (access-map-keyword r)
  "(get map :a)" (access-map-get m)
  "(map :a)" (access-map-map m)
  "(:a map)" (access-map-keyword m)
  "Accessing type/record field directly is fastest. Accessing record/map by keyword first is slowest.")

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
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "type assoc by copy" (assoc-type t)
  "record assoc by copy" (assoc-record r)
  "record assoc" (assoc-map r)
  "map assoc" (assoc-map m)
  "Assoc in map or record is slow. Much faster is to have a custom type and copy it on every assoc.")
