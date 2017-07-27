(ns vocalchat-matching.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[vocalchat-matching started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[vocalchat-matching has shut down successfully]=-"))
   :middleware identity})
