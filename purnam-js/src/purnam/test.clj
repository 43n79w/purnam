(ns purnam.test
  (:require [clojure.string :as s])
  (:use [purnam.js :only [js-expand change-roots-map cons-sym-root hash-map?]]))

(defmacro init []
  (list
   'js* "beforeEach(function(){
           this.addMatchers({
             toSatisfy: function(expected, tactual, texpected){
               var actual = this.actual;
               var notText = this.isNot ? 'Not ' : '';

               this.message = function(){
                 return 'Expression: ' + tactual +
                       '\\n   Expected result: ' + notText + texpected +
                       '\\n   Actual result: ' +  actual;}

               if(typeof(expected) == 'function'){
                 return expected(actual);
               } else { return expected === actual; }
         }})});"))

(defmacro describe [options & body]
  (let [[options body]
        (if (hash-map? options)
            [options body]
            [{} (cons options body)])]
    (js-expand
     (list 'let (or (options :bindings) [])
           (list 'js/describe (or (options :doc) "")
               `(fn [] ~@body
                  nil))))))
                  
(defmacro it [desc & body]
  (let [[desc body]
        (if (string? desc)
          [desc body]
          ["" (cons desc body)])]
    (list 'js/it desc
          `(fn [] ~@body))))

(defmacro beforeEach [& body]
  (list 'js/beforeEach `(fn [] ~@body)))

(defmacro is [v expected]
  (list '.toSatisfy (list 'js/expect v) expected (str v) (str expected)))

(defmacro is-not [v expected]
  (list '.toSatisfy (list '.-not (list 'js/expect v)) expected (str v) (str expected)))

(defmacro is-equal [v expected]
  (list '.toEqual (list 'js/expect v) expected))

(defmacro is-not-equal [v expected]
  (list '.toEqual (list '.-not (list 'js/expect v)) expected))
