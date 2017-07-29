(ns vocalchat-matching.routes.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clj-time.core :as t]
            [clj-time.format :as f]))

(defapi service-routes
  {:swagger {:ui   "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version     "1.0.0"
                           :title       "Sample API"
                           :description "Sample Services"}}}}

  (context "/api" []
    :tags ["API"]

    (POST "/error" req
      :return      String
      :body-params [message :- String,
                    filename :- String
                    linenumber :- Long,
                    stack :- String]
      :summary     "Report an error"
      (let [output-file (str "error_logs/" (f/unparse (f/formatters :basic-date-time-no-ms) (t/now))
                             ".edn")
            user-agent  (get-in req [:headers "user-agent"])
            data        {:message     message
                         :file-name   filename
                         :line-number linenumber
                         :stack       stack
                         :user-agent  user-agent}]
        (spit output-file (with-out-str (pr data)))
        (ok output-file)))))
