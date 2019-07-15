(ns authentication
  (:require
    [cheshire.core :refer [parse-string]]
    [clj-oauth2.client :as oauth2]
    [clojure.java.io :as io]
    [clojure.edn :as edn]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))

    (catch java.io.IOException e
      (printf "Couldn't open '%s': %s\n" source (.getMessage e)))
    (catch RuntimeException e
      (printf "Error parsing edn file '%s': %s\n" source (.getMessage e)))))

(def login-uri
  "https://accounts.google.com")

(def google-com-oauth2
  (let [client (load-edn "client.edn")
        client-secret ('secret client)
        client-id ('id client)]
    {:authorization-uri (str login-uri "/o/oauth2/auth")
     :access-token-uri (str login-uri "/o/oauth2/token")
     :redirect-uri "http://localhost:8080/authentication/callback"
     :client-id client-id
     :client-secret client-secret
     :access-query-param :access_token
     :scope ["https://www.googleapis.com/auth/userinfo.email"]
     :grant-type "authorization_code"
     :access-type "online"
     :approval_prompt ""}))

(def auth-req
  (oauth2/make-auth-request google-com-oauth2))

(defn auth-uri [] (:uri auth-req))

(defn offline-access-uri []
  (let [extra-params "&access_type=offline&approval_prompt=force"]
    (str (auth-uri) extra-params)))

(defn- google-access-token [request]
  (oauth2/get-access-token google-com-oauth2 (:params request) auth-req))

(defn- google-user-email [access-token]
  (let [response (oauth2/get "https://www.googleapis.com/oauth2/v1/userinfo" {:oauth access-token})]
    (get (parse-string (:body response)) "email")))

;; Redirect them to (:uri auth-req)

;; When they comeback to /authentication/callback
;;(google-user-email  ;=> user's email trying to log in
;;  (google-access-token *request*))
