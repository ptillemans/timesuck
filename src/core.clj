(ns core
  (:require [gitlab]
            [mongo]
            [gcal]))


(defn fetch-gitlab-events
  []
  (-> (gitlab/user-events-after "pti" "2018-12-31")
      (mongo/insert-gitlab-events)))

(defn fetch-gcal-events
  []
  (-> (gcal/events_since (Date. "2018/12/31"))
      (mongo/insert-gcal-events)))

(defn -main
  []
  (+ 2 2))