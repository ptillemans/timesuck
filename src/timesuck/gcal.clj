(ns timesuck.gcal
  (:require [clojure.java.io :as io]
            [clojure.walk :refer :all]
            [cheshire.core :refer :all])
  (:import [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.services.calendar CalendarScopes Calendar$Builder Calendar Calendar$Events]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]
           [com.google.api.client.googleapis.auth.oauth2 GoogleAuthorizationCodeFlow GoogleClientSecrets GoogleAuthorizationCodeFlow$Builder]
           (com.google.api.client.extensions.jetty.auth.oauth2 LocalServerReceiver LocalServerReceiver$Builder)
           (com.google.api.client.extensions.java6.auth.oauth2 AuthorizationCodeInstalledApp)
           (com.google.api.client.util.store FileDataStoreFactory)
           (java.io File)
           (java.time LocalDateTime)
           (com.google.api.services.calendar.model Event Events)
           (com.google.api.client.util DateTime)
           (java.util Date)))


(def app-name "TimeSuck")
(def json-factory (JacksonFactory/getDefaultInstance))
(def tokens-dir "tokens")
(def scopes [CalendarScopes/CALENDAR_READONLY])
(def credential-file-path "client_id.json")
(def tokens-dir "tokens")
(def http (GoogleNetHttpTransport/newTrustedTransport))

(defn get-credentials
  ;; (final NetHttpTransport HTTP_TRANSPORT)
  []
  ;;InputStream in = CalendarQuickstart.class.getResourceAsStream (CREDENTIALS_FILE_PATH) ;
  ;;if (in == null)
  ;{
  ; throw new FileNotFoundException ("Resource not found: " + CREDENTIALS_FILE_PATH) ;
  ; }
  ;GoogleClientSecrets clientSecrets = GoogleClientSecrets.load (JSON_FACTORY, new InputStreamReader (in)) ;

  (let [secrets (with-open [r (io/reader credential-file-path)]
                  (GoogleClientSecrets/load json-factory r))
        token-store (FileDataStoreFactory. (File. tokens-dir))
        flow (-> (GoogleAuthorizationCodeFlow$Builder. http json-factory secrets scopes)
                 (.setDataStoreFactory token-store)
                 (.setAccessType "offline")
                 (.build))
        receiver (-> (LocalServerReceiver$Builder.)
                     (.setPort 8888)
                     (.build))
        app (AuthorizationCodeInstalledApp. flow receiver)
        ]
    (.authorize app "user")))

(defn get-start [^Event event]
  (let [start (.getStart event)]
    (or (.getDateTime start)
        (.getDate start))))

(defn next_events
  []
  (let [credentials (get-credentials)
        service (-> (Calendar$Builder. http json-factory credentials)
                    (.setApplicationName "TimeSuck")
                    (.build))
        now (DateTime. (System/currentTimeMillis))
        events (-> service
                   (.events)
                   (.list "primary")
                   (.setMaxResults (int 10))
                   (.setTimeMin now)
                   (.setOrderBy "startTime")
                   (.setSingleEvents true)
                   (.execute))
        items (.getItems events)
        ]
    (for [item items]
      (let [start (get-start item)
            summary (.getSummary item)]
        (str summary " (" start ")")))))


(defn events_from_till
  [^Calendar service ^DateTime from ^DateTime till page results]
  (let [^Events events (-> service
                           (.events)
                           (.list "primary")
                           (.setPageToken page)
                           ;                           (.setMaxResults (int 100))
                           (.setTimeMin from)
                           (.setTimeMax till)
                           (.setOrderBy "startTime")
                           (.setSingleEvents true)
                           (.execute))
        items (map #(parse-string (.toString %) true)
                   (.getItems events))
        next_page (.getNextPageToken events)
        new_results (concat results items)]
    (if (nil? next_page)
      new_results
      (recur service from till next_page new_results))))


(defn events_since
  [^Date since]
  (let [credentials (get-credentials)
        service (-> (Calendar$Builder. http json-factory credentials)
                    (.setApplicationName "TimeSuck")
                    (.build))
        now (DateTime. (System/currentTimeMillis))
        start (DateTime. since)]
    (flatten (events_from_till service start now nil ()))))
