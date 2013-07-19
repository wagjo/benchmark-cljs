;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.page
  "HTML page manipulation."
  (:require [goog.dom :as gdom]
            [goog.dom.classes :as gcs]))

;;;; Implementation details

(def dom-doc (gdom/getDocument))

(defn find-by-id
  "Returns DOM element with a given id."
  [dom-doc id]
  (.getElementById dom-doc id))

(defn dom-element-w-text
  "Creates DOM element with given tag and text."
  [dom-doc tag text]
  (let [dom-elm (.createElement dom-doc tag)
        dom-nde (.createTextNode dom-doc text)]
    (.appendChild dom-elm dom-nde)
    dom-elm))

(defn dom-element-w-class-text
  "Creates DOM element with given tag, class and text."
  [dom-doc tag class text]
  (let [dom-elm (.createElement dom-doc tag)
        dom-nde (.createTextNode dom-doc text)]
    (gcs/add dom-elm class)
    (.appendChild dom-elm dom-nde)
    dom-elm))

(defn dom-element-w-class-html
  "Creates DOM element with given tag, class and text."
  [dom-doc tag class html]
  (let [dom-elm (.createElement dom-doc tag)]
    (gcs/add dom-elm class)
    (set! (.-innerHTML dom-elm) html)
    dom-elm))

(defn dom-conj!
  "Appends child-elm to dom-elm. Returns nil."
  [dom-elm child-elm]
  (.appendChild dom-elm child-elm)
  nil)

;;;; Public API

(defn dom-print
  "Prints messages to the web page."
  [& messages]
  (let [content-elm (find-by-id dom-doc "content")]
    (doseq [m messages]
      (dom-conj! content-elm (dom-element-w-text dom-doc "p" m)))))

(defn dom-print-benchmark
  "Prints benchmark to the web page."
  [title & reports]
  (let [content-elm (find-by-id dom-doc "content")]
    (dom-conj! content-elm
               (dom-element-w-class-html dom-doc "p" "bt" title))
    (doseq [r reports]
      (dom-conj! content-elm
                 (dom-element-w-class-html dom-doc "p" "br" r)))))

(defn wait-over!
  "Removes wait message."
  []
  (let [wait-elm (find-by-id dom-doc "wait")]
    (.removeChild (.-parentNode wait-elm) wait-elm)))
