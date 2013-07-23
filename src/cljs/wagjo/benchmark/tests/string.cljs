;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.tests.string
  "String operations."
  (:require-macros [wagjo.tools.profile]
                   [wagjo.data.string :as us]
                   [wagjo.benchmark.register :refer [defbenchmark]])
  (:require [goog.string :as gs]
            [goog.string.StringBuffer :as gSB]
            [wagjo.tools.profile]
            [wagjo.benchmark.state]
            [wagjo.data.string :as us]
            [wagjo.data.array :as ua]
            [wagjo.benchmark.preventer :refer [crunch]]))

;;; concat three strings

(defn goog-concat
  [x1 x2 x3]
  (let [sb (gs/StringBuffer.)]
    (.append sb x1 x2 x3)
    (.toString sb)))

(def gsb (gs/StringBuffer.))

(defn goog-static-concat
  [x1 x2 x3]
  (.clear gsb)
  (.append gsb x1 x2 x3)
  (.toString gsb))

(defbenchmark small-concat
  100 10000 [s1 "lorem ipsum"
             s2 "dolor sit"
             s3 (str "amet " (rand-int 10))]
  "average overhead for this benchmark" (crunch nil)
  "custom concat with js/+" (crunch (us/cat s1 s2 s3))
  "using static google.string.StringBuffer" (crunch (goog-static-concat s1 s2 s3))
  "using google.string.StringBuffer" (crunch (goog-concat s1 s2 s3))
  "(str s1 s2 s3)" (crunch (str s1 s2 s3))
  "str function is very slow for frequent string concatenation.")

;;; concat large number of strings

(def f (.-append gsb))

(defn goog-static-concat-seq
  [seq]
  (.clear gsb)
  (.apply f gsb (to-array seq))
  (.toString gsb))

(defn goog-static-concat-seq*
  [seq]
  (.clear gsb)
  (doseq [s seq]
    (.append gsb s))
  (.toString gsb))

(def ss (doall (take 3 (map #(str "aaa " %) (repeatedly #(rand-int 10))))))
(def ls (doall (take 1000 (map #(str "aaa " %) (repeatedly #(rand-int 10))))))
(def as (to-array ls))
(def vs (vec ls))

(defbenchmark small-seq-concat
  50 10000 []
  "average overhead for this benchmark" (crunch nil)
  "custom concat with reduce and js/+" (crunch (us/cat-seq ss))
  "using static google.string.StringBuffer with apply" (crunch (goog-static-concat-seq ss))
  "using static google.string.StringBuffer with doseq" (crunch (goog-static-concat-seq* ss))
  "(apply str seq)" (crunch (apply str ss))
  "str function is also slow for concatenation of string seqs.")

(defbenchmark large-seq-concat
  30 100 []
  "average overhead for this benchmark" (crunch nil)
  "custom concat with reduce and js/+" (crunch (us/cat-seq ls))
  "using static google.string.StringBuffer with apply" (crunch (goog-static-concat-seq ls))
  "using static google.string.StringBuffer with doseq" (crunch (goog-static-concat-seq* ls))
  "(apply str seq)" (crunch (apply str ls))
  "str function is also slow for concatenation of string seqs.")

(defbenchmark large-coll-concat
  30 100 []
  "average overhead for this benchmark" (crunch nil)
  "custom array concat" (crunch (us/cat-arr as))
  "array reduce" (crunch (reduce us/cat2 "" as))
  "vector reduce" (crunch (reduce us/cat2 "" vs))
  "Fastest way to concat many strings is to have them in array and concat them in the loop.")
