(ns vocalchat-matching.routes.websockets
  (:require [compojure.core :refer [GET defroutes wrap-routes]]
            [clojure.tools.logging :as log]
            [immutant.web.async :as async]
            [vocalchat-matching.signaling :as signaling]
            [vocalchat-matching.queue :as queue]
            [vocalchat-matching.transit :as t]))

(defn connect! [channel]
  (log/info "channel open")
  #_(swap! channels conj channel))

(defn disconnect! [channel {:keys [code reason]}]
  (log/info "close (code: " code ", reason: " reason ")")
  (queue/remove! channel)
  #_(swap! channels #(remove #{channel} %)))

(defn handle-message! [channel msg]
  (let [msg (t/read msg)]
    (log/info msg)
    (condp = (:type msg)
      :queue/join                  (do
                                     (queue/add! (:language msg) (:skill-level msg) channel)
                                     (queue/match-and-notify! queue/queues (:language msg) (:skill-level msg)))
      :queue/leave                 (if (and (:language msg) (:skill-level msg))
                                     (queue/remove! (:language msg) (:skill-level msg) channel)
                                     (queue/remove! channel))
      :signaling/audio-offer       (signaling/handle-offer! msg)
      :signaling/audio-answer      (signaling/handle-answer! msg)
      :signaling/new-ice-candidate (signaling/handle-new-ice-candidate! msg)
      :signaling/hangup            (signaling/handle-hangup! msg)
      (log/warn "Unsupported message type: " (:type msg)))))

(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open connect!
   :on-close disconnect!
   :on-message handle-message!})

(defn ws-handler [request]
  (async/as-channel request websocket-callbacks))

(defroutes websocket-routes
  (GET "/ws" [] ws-handler))
