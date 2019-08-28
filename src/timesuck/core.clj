(ns timesuck.core
  (:require [timesuck.gitlab :as gitlab]
            [timesuck.mongo :as mongo]
            [timesuck.gcal :as gcal]))


(comment

  (defn gitlab->mongo
    []
    (-> (gitlab/user-events-after "pti" "2018-12-31")
        (mongo/insert-gitlab-events)))

  (defn gitlab->edn []
    (let [es (gitlab/user-events-after "pti" "2018-12-31")]
      (with-open [out (clojure.java.io/writer "/tmp/gitlab.edn")]
        (.write out (pr-str es)))))

  (defn gcal->mongo
    []
    (-> (gcal/events_since (Date. "2018/12/31"))
        (mongo/insert-gcal-events))))

(defn -main
  []
  (+ 2 2))
