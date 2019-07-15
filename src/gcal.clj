(ns gcal
  (:import [com.google.api.client.json.jackson2 JacksonFactory]
           [com.google.api.services.calendar Calendar CalendarScopes]
           [com.google.api.client.googleapis.javanet GoogleNetHttpTransport]))


(def app-name "TimeSuck")
(def json-factory (JacksonFactory/getDefaultInstance))
(def tokens-dir "tokens")
(def scopes '(CalendarScopes/CALENDAR_READONLY))


(defn -main []
  (println "GCal App launched"))
