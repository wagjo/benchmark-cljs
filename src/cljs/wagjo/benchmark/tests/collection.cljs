;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.collection
  "Various benchmark for collections."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [cljs.core.rrb-vector :as rrb]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;;; Public API

;;; contains

;;; 
