(ns vocalchat-matching.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [vocalchat-matching.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[vocalchat-matching started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[vocalchat-matching has shut down successfully]=-"))
   :middleware wrap-dev})
