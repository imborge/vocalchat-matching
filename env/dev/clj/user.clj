(ns user
  (:require [mount.core :as mount]
            vocalchat-matching.core))

(defn start []
  (mount/start-without #'vocalchat-matching.core/repl-server))

(defn stop []
  (mount/stop-except #'vocalchat-matching.core/repl-server))

(defn restart []
  (stop)
  (start))


