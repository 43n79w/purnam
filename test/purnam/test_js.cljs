(ns purnam.test-js
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ? ?> ! !> f.n def.n]]
               [purnam.test :only [init describe it is is-not
                                   is-equal is-not-equal]]))

(init)



(describe
 {:doc "objs contain js arrays"
  :bindings [o1 (obj :array [1 2 3 4])]}

 (it "describes something"
     (is o1.array.0 odd?)
     (is o1.array.1 2)
     (is o1.array.2 3)
     (is o1.array.3 4)
     (is o1.array.4 nil)
     (! o1.array.4 5)
     (is o1.array.4 5)
     (is-not o1.array (array 1 2 3 4 5))
     (is-equal o1.array (array 1 2 3 4 5))
     (is-not (array 1 2 3 4) (array 1 2 3 4))
     (is-equal (array 1 2 3 4) (array 1 2 3 4))))

(describe
  {:doc "my first test using purnam"
   :bindings [o1 (obj :array [1 2 3 4])]}

  (it "describes something"
      (is o1.array.0 odd?)
      (is o1.array.1 even?)
      (is o1.array.2 odd?)
      (is o1.array.3 4)
      (is o1.array.4 nil)))

(describe
 {:doc "obj"
  :bindings [o1 (obj :array [1 2 3 4])
             o2 (obj :a 1 :b 2 :c 3)
             n1 "a" n2 "b" n3 "c"]}
 (it
  "can create js-objects and allow arbitrary accessors"
  (is o2.a 1)
  (is o2.b 2)
  (is o2.b even?)
  (is o2.b (fn [v] (= v 2)))
  (is o2.c 3)
  (is o2.|n1| 1)
  (is o2.|n2| 2)
  (is o2.|n3| 3)
  (is o1.array.|o2.a| 2)
  (is o1.array.0 1)
  (is o1.array.|o1.array.0| 2)
  (is o1.array.|o1.array.|o1.array.0|| 3)
  (is o1.array.|o1.array.|o1.array.|o1.array.0||| 4)
  (is o2.d nil)))

(describe
 {:doc "obj"
  :bindings [name array
             o1   (obj name [1 2 3 4])]}
 (it "can do things"
     (is o1.|name|.0 1)))

(describe
 {:doc "obj.self refers to the object"
  :bindings [o3 (obj :a 2 :fn (fn [] self.a))
             o4 (obj :a 3 :fn o3.fn)
             fn1  o3.fn]}
 (it
  "is different to `this` in js"
  (is (aget (aget-in o3 []) "a") 2)
  (is (o3.fn) 2)
  (is (o4.fn) 2)
  (is (fn1) 2)
  (! o3.a 4)
  (is (o3.fn) 4)
  (is (o4.fn) 4)
  (is (fn1) 4)))

(describe
 {:doc "self and this"
  :bindings [a1 (obj :a 1
                     :thisfn (fn [] this.a)
                     :selffn (fn [] self.a))
             a2 (obj :a 2
                     :thisfn a1.thisfn
                     :selffn a1.selffn)]}
  (it "will show the difference"
      (is (a1.thisfn) 1)
      (is (a1.selffn) 1)
      (is (a2.thisfn) 2)
      (is (a2.selffn) 1)))

(describe
  {:doc "obj.self will match the scope that it is declared in"
   :bindings
   [a1 (obj :a 1
            :b {:a 2
                :fn (fn [] self.a)})
    a2 (obj :a 1
            :b  (obj :a 2
                   :fn (fn [] self.a)))]}
 (it "Can run functions"
     (is (a1.b.fn) 1)
     (is (a2.b.fn) 2)))

(describe
 {:doc "? and !"
  :bindings
  [a (js-obj)
   _ (! a.b.c.d.e "e")
   b (? a.b)
   c (? b.c)
   d (? c.d)
   e (? d.e)
   _ (! d.e "f")]}
 (it "does the right thing"
     (is e "e")
     (is (? d.e) "f")
     (is-not (? d.e) e)))


(describe
  {:doc "! - Example 5.1"
   :bindings [a (obj)]}
  (! a.b 1)
  (! a.b.c 1)
  (it "will not set "
    (is a.b.c nil)
    (is a.b 1)))

(describe
  {:doc "! - Example 5.2"
   :bindings
   [o (obj)
    k "a"]}
  (! o.|k| 6)
  (it "will set o.a"
    (is o.a 6)))

#_(describe
  {:doc "! - Example 5.3"
   :bindings
   [o (obj :a 1)]}
  (! o (obj :a 2)) ;; => throws exception
  (it "will not rebind o.a"
    (is o.a 1)))

(describe
  {:doc "!> - Example 6.2"
   :bindings
   [o (obj :func (fn [] 6))
    k "func"]}
  (it "can use pipe notation to call"
    (is (o.|k|) 6)))


(describe
  {:doc "equivalence of objects"
   :bindings
 [a (js-obj)
  _ (! a.b.c.d.e "e")
  b (? a.b)
  c (? b.c)
  d (? c.d)
  e (? d.e)]}
 (it "uses js"
     (is b (? a.b))
     (is c (? a.b.c))
     (is d (? a.b.c.d))
     (is e (? a.b.c.d.e))))
