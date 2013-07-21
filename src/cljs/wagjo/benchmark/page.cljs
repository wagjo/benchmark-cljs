;; Copyright (C) 2013, Jozef Wagner. All rights reserved.

(ns wagjo.benchmark.page
  "HTML page manipulation."
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! close! chan timeout]]
            [goog.dom :as gdom]
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

(defn dom-element-w-class
  "Creates DOM element with a given tag and class."
  [dom-doc tag class]
  (let [dom-elm (.createElement dom-doc tag)]
    (gcs/add dom-elm class)
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
  [title notes & reports]
  (let [content-elm (find-by-id dom-doc "content")]
    (dom-conj! content-elm
               (dom-element-w-class-html dom-doc "p" "bt" title))
    (doseq [r reports]
      (dom-conj! content-elm
                 (dom-element-w-class-html dom-doc "p" "br" r)))
    (dom-conj! content-elm
               (dom-element-w-class-html dom-doc "p" "bn" (str "Expected outcomes: " notes)))))

(defn wait-over!
  "Removes wait message."
  []
  (let [wait-elm (find-by-id dom-doc "wait")]
    (.removeChild (.-parentNode wait-elm) wait-elm)))

(defn run-started!
  "Removes wait message."
  []
  (let [elm (find-by-id dom-doc "content")]
    (set! (.-innerHTML elm)
          "<p id=\"wait\">Computing...</p>")))

(defn run-tests
  [c]
  (go
   (<! (timeout 10)) ;; allow page to repaint
   (loop [b (<! c)]
     (if (nil? b)
       (wait-over!)
       (do
         (let [[t & r] ((:fn b))]
           (apply dom-print-benchmark t (:notes b) r))
         (recur (<! c)))))))

(defn run-group
  [group]
  (run-started!)
  (let [c (chan)]
    (run-tests c)
    (go
     (let [sorted group]
       (doseq [b sorted]
         (>! c b))
       (close! c)))))

(defn run-bench
  [bench]
  (run-started!)
  (let [c (chan)]
    (run-tests c)
    (go (>! c bench)
        (close! c))))

(defn create-menu-group
  [group]
  (let [elm (dom-element-w-class-text dom-doc "a" "bg"
                                      (:group (first group)))
        handler (fn [evt] (run-group group))]
    (.setAttribute elm "href" "#")
    (.addEventListener elm "click" handler)
    elm))

(defn create-menu-bench
  [b]
  (let [elm (dom-element-w-class-text dom-doc "a" "bb"
                                      (:name b))
        handler (fn [evt] (run-bench b))]
    (.setAttribute elm "href" "#")
    (.addEventListener elm "click" handler)
    elm))

(defn populate-menu
  "Populates menu with benchmarks."
  [benchmarks]
  (let [group-seq (map second
                       (sort-by first (group-by :group benchmarks)))
        menu-elm (find-by-id dom-doc "menu")]
    (doseq [group group-seq]
      (let [new-line-elm (dom-element-w-class dom-doc "div" "mlg")]
        (dom-conj! menu-elm new-line-elm)
        (dom-conj! new-line-elm (create-menu-group group)))
      (doseq [b group]
        (let [new-line-elm (dom-element-w-class dom-doc "div" "ml")]
          (dom-conj! menu-elm new-line-elm)
          (dom-conj! new-line-elm (create-menu-bench b)))))
    (let [new-line-elm (dom-element-w-class dom-doc "div" "mlf")]
      (set! (.-innerHTML new-line-elm)
            "Want more tests?<br/><a href=\"https://github.com/wagjo/benchmark-cljs/pulls\">pull requests</a> are welcome!")
      (dom-conj! menu-elm new-line-elm))))
