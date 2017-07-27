(ns vocalchat-matching.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [vocalchat-matching.routes.services :refer [service-routes]]
            [vocalchat-matching.routes.websockets :refer [websocket-routes]]
            [compojure.route :as route]
            [vocalchat-matching.env :refer [defaults]]
            [mount.core :as mount]
            [vocalchat-matching.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    #'websocket-routes
    (route/not-found
      "page not found")))


(defn app [] (middleware/wrap-base #'app-routes))
