(ns ^:figwheel-hooks my-app-ns.cards
  (:require [devcards.core]))

(enable-console-print!)

(defn render []
  (devcards.core/start-devcard-ui!))

(defn ^:after-load render-on-relaod []
  (render))

(render)
