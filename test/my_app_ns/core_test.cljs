(ns my-app-ns.core-test
  (:require [my-app-ns.core :as sut]
            [cljs.test :as t :include-macros true]))


(t/deftest simple
  (t/is (= 4 (+ 2 2))))
