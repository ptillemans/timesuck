(ns ^:figwheel-hooks my-app-ns.test-runner
  (:require  [my-app-ns.core-test]
             [cljs.test :as test]
             [cljs-test-display.core :as display])
  )

(enable-console-print!)

(defn test-run []
  (test/run-tests
   (display/init! "app-tests")
   ))

;(defn ^:after-load render-on-reload []
;  (test-run))

;(test-run)
