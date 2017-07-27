(ns vocalchat-matching.signaling
  (:require [immutant.web.async :as async]
            [vocalchat-matching.rooms :as rooms]
            [vocalchat-matching.transit :as t]))

(defn handle-offer! [{:keys [target room-uuid] 
                      :as msg}]
  (let [{:keys [users]} (@rooms/rooms room-uuid)]
    (async/send! (target users) (t/write msg))))

(defn handle-answer! [{:keys [target room-uuid] 
                       :as msg}]
  (let [{:keys [users]} (@rooms/rooms room-uuid)]
    (async/send! (target users) (t/write msg))))

(defn handle-new-ice-candidate! [{:keys [target room-uuid]
                                  :as msg}]
  (let [{:keys [users]} (@rooms/rooms room-uuid)]
    (async/send! (target users) (t/write msg))))

(defn handle-hangup! [{:keys [caller? room-uuid]}]
  (let [{:keys [users]} (@rooms/rooms room-uuid)
        target (if caller?
                 :callee
                 :caller)]
    (async/send! (target users) (t/write {:type :signaling/hangup}))
    (rooms/remove! room-uuid)))
