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
  (abuse [_]
    (crunch nil) (crunch nil))
  P
  (-simple [_]
    (crunch nil) (crunch nil)))

(defn- ^:export no-poly []
  (crunch nil) (crunch nil))

(defn- ^:export abuse-wrap [t]
  (.abuse t))

(defbenchmark protocol
  50 10000 [la 1
            lb 2
            ttt (A.)]
  "baseline" (do (crunch nil) (crunch nil))
  "no polymorphism" (no-poly)
  "calling protocol fn" (-simple ttt)
  "calling protocol fn with not-native hint"
  (-simple ^not-native ttt)
  "abusing js/Object" (abuse-wrap ttt)
  "Calling polymorphic function is slower. Abusing Object fixes this (only slightly in Chrome), but has other drawbacks. ^not-native is the best solution, if you can use it.")
