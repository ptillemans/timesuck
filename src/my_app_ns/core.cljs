(ns my-app-ns.core)

(def canvas (.getElementById js/document "canvas"))
(def ctx (.getContext canvas "2d"))

(defn draw-shape [x y radius color]
  (set! (.-fillStyle ctx) color)
  (.beginPath ctx)
  (.arc ctx x y radius 0 (* 2 Math/PI))
  (.fill ctx))

(draw-shape 150 150 100 "blue")
