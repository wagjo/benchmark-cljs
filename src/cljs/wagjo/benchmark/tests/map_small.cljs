;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.map-small
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

(def kvv [[:a (rand-int 10)] [:b (rand-int 10)]
          [:c (rand-int 10)] [:d (rand-int 10)]
          [:e (rand-int 10)] [:f (rand-int 10)]
          [:g (rand-int 10)] [:h (rand-int 10)]])

(defn create-conj
  [kvv]
  (apply conj {} kvv))

(defn create-into
  [kvv]
  (into {} kvv))

(defn create-reduce
  [kvv]
  (reduce conj {} kvv))

(defbenchmark create-dynamic
  50 1000 []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "(into {} ...)" (do (crunch nil) (crunch (create-into kvv)))
  "(reduce conj {} ...)" (do (crunch nil) (crunch (create-reduce kvv)))
  "(apply conj {} ...)" (do (crunch nil) (crunch (create-conj kvv)))
  "Creating map with into is both fast and idiomatic.")

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

(def m (create-map))
(def t (create-type))
(def r (create-record))
(def a (rand-int 10))

(defbenchmark access
  50 10000
  []
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

(defn- ^:export update-map [m]
  (crunch nil) (crunch (update-in m [:d] inc)))

(defn- ^:export assoc-type [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-type a b 8 d e))))

(defn- ^:export update-type [o]
  (let [a (get-a o)
        b (get-b o)
        c (get-c o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-type a b c (inc d) e))))

(defn- ^:export assoc-record [o]
  (let [a (get-a o)
        b (get-b o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-record a b 8 d e))))

(defn- ^:export update-record [o]
  (let [a (get-a o)
        b (get-b o)
        c (get-c o)
        d (get-d o)
        e (get-e o)]
    (crunch nil)
    (crunch (create-my-record a b c (inc d) e))))

(def m2 (create-map))
(def t2 (create-type))
(def r2 (create-record))

(defbenchmark assoc
  50 10000
  []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "type assoc by copy" (assoc-type t2)
  "record assoc by copy" (assoc-record r2)
  "record assoc" (assoc-map r2)
  "map assoc" (assoc-map m2)
  "Assoc in map or record is slow. Much faster is to have a custom type and copy it on every assoc.")

(def m3 (create-map))
(def t3 (create-type))
(def r3 (create-record))

(defbenchmark update
  50 10000
  []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "type update by copy" (update-type t3)
  "record update by copy" (update-record r3)
  "record update" (update-map r3)
  "map update" (update-map m3)
  "Update in map or record is slow. Much faster is to have a custom type and copy it on every update.")
