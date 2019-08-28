(ns timesuck.gitlab
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [cheshire.core :refer :all]
            [clj-http.client :as client])
  (:import (java.io PushbackReader)))

(def token-filename "tokens/gitlab.edn")

(defn gitlab-config
  "read the configuration "
  [filename]
  (with-open
    [r (io/reader filename)]
    (edn/read (PushbackReader. r))))

(def cached-gitlab-config (memoize gitlab-config))

(defn gitlab-token
  "read the gitlab token from the token file"
  ([config]
   (:gitlab-token config))
  ([]
   (gitlab-token (cached-gitlab-config token-filename))))

(defn gitlab-uri
  ([config]
   (:gitlab-uri config))
  ([] (gitlab-uri (cached-gitlab-config token-filename))))

(defn api-url [endpoint]
  (str (gitlab-uri) endpoint))

(defn parse-response [response]
  (parse-string
   (get response :body) true))

(defn url-get-paginated
  [url options results]
  (let [response (client/get url options)
        result (parse-response response )
        next (-> response (:links) (:next) (:href))]
    (if (nil? next)
      (conj results result)
      (recur next options (conj results result)))))

(defn api-raw-get
  [endpoint]
  (url-get-paginated
    (api-url endpoint)
    {:headers {"PRIVATE-TOKEN" (gitlab-token)}
     :accept  :json}
    ()))

(defn api-get
  ([endpoint]
   (flatten (api-raw-get endpoint))))

(defn user-events-after
  [user after]
  (let [endpoint (str "/users/" user "/events?after=" after)]
    (api-get endpoint)))
