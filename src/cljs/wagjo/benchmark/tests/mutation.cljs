;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.mutation
  "Playing with mutation."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; mutation

(defprotocol IMutable
  (-set-x [t new-x]))

(deftype MutableType [^:mutable x]
  IMutable
  (-set-x [_ new-x]
    (set! x new-x)))

(defn ^:export reset!*
  [type val]
  (crunch nil)
  (-set-x ^not-native type val))

(def mutable-atom (atom nil))
(def mutable-type (MutableType. nil))
(def r (rand-int 10))

(defbenchmark atom
  200 20000 []
  "average overhead for this benchmark" (crunch nil)
  "mutating type" (reset!* mutable-type r)
  "mutating atom" (do (crunch nil) (reset! mutable-atom r))
  "Mutating using custom type is faster than atom, but you loose additional functionality like watchers.")
