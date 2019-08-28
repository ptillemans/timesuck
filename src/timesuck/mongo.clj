(ns timesuck.mongo
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import (org.bson.types ObjectId)))

(def conn (mg/connect))

(def db (mg/get-db conn "timeSuckDB"))


(defn insert-gitlab-event
  [event]
  (let [id (ObjectId.)
        doc (assoc event :_id (:created_at event))]
    (mc/insert db "gitlabEvents" doc)))


(defn insert-gitlab-events
  [events]
  (doseq [event events]
    (insert-gitlab-event event)))

(defn insert-gcal-event
  [event]
  (let [id (ObjectId.)
        doc (assoc event :_id (:id event))]
    (mc/insert db "gcalendarEvents" doc)))


(defn insert-gcal-events
  [events]
  (doseq [event events]
    (insert-gcal-event event)))

