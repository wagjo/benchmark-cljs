;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.preventer
  "Prevents closure compiler to remove otherwise useless functions.")

;;;; Implemnetation details

(deftype MutableCounter [^:mutable counter]
  Object
  (increment [c] (set! counter (inc counter))))

(def ^:private useless (MutableCounter. 0))

(def ^:private unique (js-obj))

;;;; Public API

(defn crunch
  "Returns input argument.
  Prevents closure compiler to remove otherwise useless functions."
  [o]
  (when (identical? unique o)
    (.increment useless))
  o)
