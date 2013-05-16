(ns purnam.test-angular
  (:use [purnam.cljs :only [aget-in aset-in]])
  (:require [goog.object :as o])
  (:require-macros [purnam.js :as j])
  (:use-macros [purnam.js :only [obj ! defv.ndef.n]]
               [purnam.test :only [init describe it is is-not is-equal is-not-equal]]
               [purnam.angular :only [def.module def.config def.controller def.service]]
               [purnam.test.angular :only [describe.controller describe.ng service]]))



;;
;; Defining our Module
;;

(def.module sample [])


;;
;; Defining a SimpleService
;;

(def.service sample.SimpleService []
  (obj :user {:login "login"
              :password "secret"
              :greeting "hello world"}
       :changeLogin (fn [login]
                      (! this.user.login login))))

;;
;; Angular Module Testing for Example Service
;;

(describe.ng
 "A sample Angular Test Suite"
 {:module sample
  :bindings [compare (obj :login "login"
                          :password "secret"
                          :greeting "hello world")]}
 (service
  "ExampleService Basics"
  [ExampleService]
  (is-not ExampleService.user compare)
  (is-equal ExampleService.user compare))

 (service
  "ExampleService Change Login"
  [ExampleService]
  (is ExampleService.user.login "login")

  (do (ExampleService.changeLogin "newLogin")
      (is ExampleService.user.login "newLogin")))

 (service ;; The login will reset
  "ExampleService Change Login"
  [ExampleService]
  (is ExampleService.user.login "login")))



;;
;; Angular Test Controller Example
;;

(def.controller sample.SimpleCtrl [$scope]
  (! $scope.msg "Hello")
  (! $scope.setMessage (fn [msg] (! $scope.msg msg))))


;;
;; Controller Testing
;;

(describe.controller
 "A sample controller for testing purposes"
 {:module sample
  :controller SimpleCtrl}

 (it "should have an object called `spec`"
     (is-not spec js/undefined))

 (it "should set a message within the $scope"
     (is spec.$scope.msg "Hello")  ;; The $scope is automatically registered for us
     (is $scope.msg "Hello")      ;; We can also use spec.$scope
     )

 (it "should be able to change the message within the $scope"
  (do ($scope.setMessage "World!")
      (is $scope.msg "World!"))

  (do ($scope.setMessage "Angular Rocks!")
      (is $scope.msg "Angular Rocks!"))))
