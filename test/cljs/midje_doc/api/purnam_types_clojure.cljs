(ns midje-doc.api.purnam-types-clojure
  (:require [purnam.core])
  (:use-macros [purnam.core :only [f.n def.n obj arr]]
               [purnam.test :only [describe is it fact facts]]))

[[:chapter {:title "purnam.types" :tag "purnam-types"}]]

"Clojure protocols for javascript native objects and arrays."

[[:section {:title "init" :tag "init-types"}]]

"There is a dependency on [purnam.native](#purnam-cljs) and so the following MUST be placed in your project namespace in order to use this protocol"

(comment
  (:use [purnam.native :only [aget-in aset-in]])
  (:require [purnam.types :as types]))


[[:section {:title "seq protocol"}]]

(facts [[{:doc "seq protocol"}]]

  "The clojure `seq` protocol allow native js arrays and objects to be accessible like clojure vectors and maps:"

  (seq (arr 1 2 3 4)) 
  => '(1 2 3 4)
  
  (seq (obj :a 1 :b 2))
  => '(["a" 1] ["b" 2]) 
  
  "As well as all the built-in functionality that come with it. Although the datastructure then becomes a clojurescript list."
  
  (map #(* 2 %) (arr 1 2 3 4))
  => '(2 4 6 8)
  
  (take 2 (arr 1 2 3 4))
  => '(1 2)
  
  (count (arr 1 2 3 4))
  => 4
  
  (get (arr 1 2 3 4) "1")
  => 2
  
  (get-in (obj :a {:b {:c 1}}) ["a" "b" "c"])
  => 1)

[[:section {:title "transient protocol"}]]

(facts [[{:doc "transient protocol"}]]

  "The clojure transinet protocol allow native js objects arrays to be manipulated using transient methods"

  (let [o (obj)]
    (assoc! o :a 1)
    o) 
  => (obj :a 1)
  
  (let [o (obj :a 1)]
    (dissoc! o :a)
    o) 
  => (obj)
  
  (persistent! (obj :a 1))
  => {:a 1}
  )

[[:section {:title "collection protocol"}]]

"The collection protocols allow native js objects arrays to use `conj`, `assoc` and `dissoc` methods."

(fact
    (def o (obj))
    (def o1 (conj o [:a 1]))
    o => (obj)
    o1 => (obj :a 1))    

(facts [[{:doc "collection protocol"}]]
  
  "It works with both arrays and objects"
  
  (conj (arr 1) 2 3)
  => (arr 1 2 3)
  
  (conj (obj :a 1) [:b 2] [:c 3])
  => (obj :a 1 :b 2 :c 3)

  (assoc (obj :a 1) :b 2 :c 3)
  => (obj :a 1 :b 2 :c 3)

  (dissoc (obj :a 1) :a)
  => (obj))