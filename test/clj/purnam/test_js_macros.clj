(ns purnam.test-js-macros
  (:use midje.sweet
        purnam.checks
        [purnam.js :only [obj]])
  (:require [purnam.js :as j]))

(fact "split-dotted"
  (j/split-dotted "a") => ["a"]
  (j/split-dotted "a.b") => ["a" "b"]
  (j/split-dotted "a.b.c") => ["a" "b" "c"]
  (j/split-dotted "a.||") => ["a" "||"]
  (j/split-dotted "a.|b|.c") => ["a" "|b|" "c"]
  (j/split-dotted "a.|b|.|c|") => ["a" "|b|" "|c|"]
  (j/split-dotted "a.|b.c|.|d|") => ["a" "|b.c|" "|d|"]
  (j/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (j/split-dotted "a.|b.|c||.|d|") => ["a" "|b.|c||" "|d|"]
  (j/split-dotted "a.|b.|c.d.|e|||.|d|") => ["a" "|b.|c.d.|e|||" "|d|"])

(fact "split-dotted exceptions"
  (j/split-dotted "|a|") => (throws Exception)
  (j/split-dotted "a|") => (throws Exception)
  (j/split-dotted "a.") => (throws Exception)
  (j/split-dotted "a.|||") => (throws Exception)
  (j/split-dotted "a.|b.|e|") => (throws Exception))

(fact "symbol-with-ns?"
  (j/symbol-with-ns? 'clojure.core/add) => true
  (j/symbol-with-ns? 'js/console) => true
  (j/symbol-with-ns? 'add) => falsey
  (j/symbol-with-ns? 'js/console.log) => falsey
  (j/symbol-with-ns? 'js/console.log) => falsey)

(fact "js-exp?"
  (j/js-exp? 'add) => false
  (j/js-exp? 'js/console) => false
  (j/js-exp? 'java.util.Set.) => false
  (j/js-exp? 'java.math.BigInteger/probablePrime) => false
  (j/js-exp? 'clojure.core/add) => false
  (j/js-exp? 'clojure.core) => true
  (j/js-exp? 'x.y/a.|b|.c) => true
  (j/js-exp? 'x.|y|.a) => true)

(fact "js-split-first"
  (j/js-split-first 'js/console.log) => '("js/console" ".log")
  (j/js-split-first 'a.b.c) => ["a" ".b.c"]
  (j/js-split-first 'a|b|.b.c) => nil
  (j/js-split-first 'js/console) => nil)

(fact "js-split-syms"
  (j/js-split-syms 'js/console.log) => ["js/console" "log"]
  (j/js-split-syms 'a.b.c/d.e.f) => ["a.b.c/d" "e" "f"]
  (j/js-split-syms 'a.b.c) => ["a" "b" "c"]
  (j/js-split-syms 'a.|b|.b.c) => ["a" "|b|" "b" "c"]
  (j/js-split-syms 'a|b|.b.c) => (throws Exception)
  (j/js-split-syms 'a.|b|./b.c) => (throws Exception)
  (j/js-split-syms 'a.|b|.c/b.c) => (throws Exception)
  (j/js-split-syms 'ns/a.|ns/b.c|) => ["ns/a" "|ns/b.c|"]
  (j/js-split-syms 'ns/b.c) => ["ns/b" "c"])

(fact "match"
  (match '(1 1) '(1 1)) => true
  (match '(1 1) '(%1 %1)) => true
  (match '(1 2) '(%1 %1)) => false
  (match '(let [x 1] (+ x 2))
            '(let [%x 1] (+ %x 2)))
  => true
  '(let [G__42879 (js-obj)]
     (aset G__42879 "a" (fn [] (? G__42879.val)))
     (aset G__42879 "val" 3) G__42879)
  =>
  (matches '(let [%x (js-obj)]
              (aset %x "a" (fn [] (? %x.val)))
              (aset %x "val" 3) %x)))


;; Macros
(fact "!"
  '(j/! hello.there 10)
  => (expands-into
      '(purnam.cljs/aset-in hello ["there"] 10))

  '(j/! hello.there.again 10)
  => (expands-into
      '(purnam.cljs/aset-in hello ["there" "again"] 10))

  '(j/! a.|b|.c 10)
  => (expands-into
      '(purnam.cljs/aset-in a [b "c"] 10))

  '(j/! a.|b.c|.d 10)
  => (expands-into
      '(purnam.cljs/aset-in a [(purnam.cljs/aget-in b ["c"]) "d"] 10)))

(fact "!>"
  '(j/!> hello.lib.add 1 2 3 4 5)
  => (expands-into
      '(let [obj# (purnam.cljs/aget-in hello ["lib"])]
         (.add obj# 1 2 3 4 5))))

(fact "?"
  '(j/? hello.there)
  => (expands-into
      '(purnam.cljs/aget-in hello ["there"]))

  '(j/? hello.there.again)
  => (expands-into
      '(purnam.cljs/aget-in hello ["there" "again"])))

(fact "def.n"
  (macroexpand-1
   '(j/def.n app-func [p x]
      (if p.module.name
        x.one
        (x.func 1 2 3))))
  =>
  '(clojure.core/defn app-func [p x]
     (if (purnam.cljs/aget-in p ["module" "name"])
       (purnam.cljs/aget-in x ["one"])
       (let [obj# (purnam.cljs/aget-in x [])]
         (.func obj# 1 2 3)))))

(fact "has-root?"
  (j/has-root? 'hello 'hello) => true
  (j/has-root? 'hello 'NONE) => false
  (j/has-root? 'hello.there 'hello) => true
  (j/has-root? 'hello.there 'hello.there) => false
  (j/has-root? 'hello.there 'NONE) => false
  (j/has-root? 'hello.there 'NONE) => false
  )

(fact "change-root"
  (j/change-sym-root 'hello 'change) => 'change
  (j/change-sym-root 'hello.there 'change) => 'change.there
  (j/change-sym-root 'hello.there.again 'change) => 'change.there.again)

(fact "walk-and-transform"
  (j/walk-and-transform '(1 2 3 4) even? odd? inc)
  => '(1 3 3 5)

  (j/walk-and-transform '(a.b c.d a a)
                    #(j/has-root? % #{'a})
                    ::none
                    (fn [x] 3))
  => '(3 c.d 3 3)

  (j/walk-and-transform '(a.b c.d a a)
                    #(j/has-root? % {'a 'A.B})
                    ::none
                    (fn [x] (j/change-sym-root
                            x
                            ({'a 'A.B} (j/get-root x)))))
  => '(A.B.b c.d A.B A.B))

(fact "change-roots"
  (j/change-roots 'hello 'hello 'change) => 'change
  (j/change-roots 'hello.there 'hello 'change) => 'change.there
  (j/change-roots 'hello.there.again 'hello 'change) => 'change.there.again
  (j/change-roots ['hello.there] 'hello 'change) => '[change.there]
  (j/change-roots {:a 'hello.there} 'hello 'change) => '{:a change.there})

(fact "change-roots-map"
  (j/change-roots-map 'hello {'hello 'change}) => 'change
  (j/change-roots-map 'hello.there {'hello 'change}) => 'change.there
  (j/change-roots-map 'hello.there.again {'hello 'change}) => 'change.there.again
  (j/change-roots-map ['hello.there] {'hello 'change}) => '[change.there]
  (j/change-roots-map {:a 'hello.there} {'hello 'change}) => '{:a change.there})

(fact "obj"
  (macroexpand-1
   '(obj :a 1  :fn (fn [] (+ self.a
                            this.a))))
  => (matches '(this-as %y
                (let [%x (js-obj)]
                  (aset %x "a" 1)
                  (aset %x "fn"
                        (fn [] (+ (purnam.cljs/aget-in %x ["a"])
                                 (purnam.cljs/aget-in %y ["a"]))))
                  %x))))

(fact "obj"
  (macroexpand-1
   '(obj :a 1
         :b  (obj :a 2
                  :fn (fn [] self.a))))
  => (matches '(this-as %y
                (let [%x (js-obj)]
                  (aset %x "a" 1)
                  (aset %x "b" (obj :a 2 :fn (fn [] self.a)))
                  %x))))
