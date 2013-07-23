;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.polymorphism
  "Function polymorphism."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; calling protocol fns

(defprotocol P
  (-simple [t]))

(deftype A []
  Object
  (abuse [_] (crunch nil) (crunch nil))
  (typecheck [_] :a)
  P
  (-simple [_] (crunch nil) (crunch nil)))

(defn- ^:export no-poly []
  (crunch nil) (crunch nil))

(defn- ^:export abuse-wrap [t]
  (.abuse t))

(def ttt (A.))

(defbenchmark protocol
  200 20000 []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "calling a function" (no-poly)
  "calling a function extended on js/Object" (abuse-wrap ttt)
  "calling a protocol function with not-native hint"
  (-simple ^not-native ttt)
  "calling a protocol function" (-simple ttt)
  "Calling polymorphic function is slower. Abusing Object fixes this (only slightly in Chrome), but has other drawbacks. ^not-native is the best solution, if you can use it.")

(defprotocol P2
  (-simple2 [t]))

(deftype B []
  Object
  (typecheck [_] :a)
  P2
  (-simple2 [_] (crunch nil) (crunch nil)))

(def t (first [:a]))
(def ttt2 (B.))

(defbenchmark type-check
  200 20000 []
  "average overhead for this benchmark" (do (crunch nil) (crunch nil))
  "(instance? T o)" (do (crunch nil) (crunch (instance? B ttt2)))
  "custom typecheck" (do (crunch nil) (crunch (== t (.typecheck ttt2))))
  "(identical? T (type o))" (do (crunch nil) (crunch (== B (type ttt2))))
  "(satisfies? P o)" (do (crunch nil) (crunch (satisfies? P2 ttt2)))
  "(satisfies? P o false)" (do (crunch nil) (crunch (satisfies? P2 ttt2 false)))
  "instance? is slower than custom type check in FF. No hacks needed for Chrome.")
