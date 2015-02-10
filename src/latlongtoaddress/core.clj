(ns latlongtoaddress.core
  (:require 
   [clj-http.client :as client]
   [clojure.data.json :as json]))


;;; This is a simple program
;;; I'll specify the lat and long
;;; then make a request to google's reverse
;;; geo coding api to get the textual
;;; address

(declare parse-google-response get-address)

(defn get-google-response [the-lat the-long]
  (let [base-url "http://maps.googleapis.com"
        path "/maps/api/geocode/json?latlng="
        final-url (str base-url path the-lat "," the-long 
                       "&sensor=true")
        google-response (client/get final-url)]
    #_(println "Response from google:" google-response)
    (if (== (:status google-response) 200)
      (parse-google-response (:body google-response))
      (println "Google NOT available"))))

(defn parse-google-response [query-results]
  (let [clj-map (json/read-str query-results 
                               :key-fn keyword)
        results (:results clj-map)
        status (:status clj-map)]
    (if (= status "OK")
      (get-address results)
      (println "No results for address"))))

(defn get-address [results]
  (let [num-results (count results)
        poss-address (for [a-result results
                           :let [types (:types a-result)]
                           :when (and (== (count types) 2))]
                       (:formatted_address a-result))]
    (println "Num results: " num-results)
    (println "Possible addresses: " poss-address)))

(defn main []
  (get-google-response 6.4410553 3.4821904))
