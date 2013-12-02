(ns angular-demos.sfp2
  (:use [purnam.native :only [aget-in aset-in]])
  (:use-macros [purnam.core :only [obj arr ! !> def.n]]
               [purnam.angular :only
                [def.module def.config def.factory def.value def.constant
                 def.provider def.service def.controller]]))

(def.module sfpTwoDemo [])

(def.constant sfpTwoDemo.magicNumber 42)
(def.constant sfpTwoDemo.bookTitle "HichHiker's Guide")

(def.controller sfpTwoDemo.UsingConstantCtrl [$scope magicNumber bookTitle]
  (! $scope.magicNumber magicNumber)
  (! $scope.bookTitle bookTitle))


(def.value sfpTwoDemo.SfpValueInstance
  (obj :getMagicNumber (fn [] 42)
       :getBookTitle (fn [] "HitchHiker's Guide")))

(def.controller sfpDemo.UsingValueCtrl [$scope SfpValueInstance]
  (! $scope.magicNumber (SfpValueInstance.getMagicNumber))
  (! $scope.bookTitle (SfpValueInstance.getBookTitle)))
