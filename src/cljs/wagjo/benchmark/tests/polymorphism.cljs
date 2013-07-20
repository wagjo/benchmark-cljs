;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.polymorphism
  "Function polymorphism."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]))

;;; arguments

(def a 1)
(def b 2)
(def c 3)

(defprotocol P
  (-simple [t lb]))

(deftype A [ta]
  Object
  (abuse [_ lb]
    [ta lb c])
  P
  (-simple [_ lb]
    [ta lb c]))

(defn- no-poly [la lb]
  [la lb c])

(defn- simple [llt lb]
  (-simple llt lb))

(defn- abuse [llt lb]
  (.abuse llt lb))

(defbenchmark protocol
  50 10000 [la 1
            lb 2
            ttt (A. la)]
  "direct computation" [la lb c]
  "no polymorphism" (no-poly la lb)
  "simple protocol function ↓" (simple ttt lb)
  "directly calling protocol fn ↓" (-simple ttt lb)
  "abusing js/Object" (abuse ttt lb)
  "directly abusing js/Object" (.abuse ttt lb))
