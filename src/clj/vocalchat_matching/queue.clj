(ns vocalchat-matching.queue
  (:require [immutant.web.async :as async]
            [vocalchat-matching.rooms :as rooms]
            [vocalchat-matching.transit :as t])) 

(def languages [:english :french :spanish :german])
(def skill-levels [:beginner :conversational :fluent])

(defn build-queues-map [languages skill-levels]
  (into {} (map (fn [lang]
                  [lang (into {} (map (fn [skill]
                                        [skill clojure.lang.PersistentQueue/EMPTY]) skill-levels))]) languages)))

(def queues (ref (build-queues-map languages skill-levels)))

(defn queued? 
  ([language skill-level chan]
   (boolean (some true? (map (fn [queued-chan]
                               (= chan queued-chan)) (get-in @queues [language skill-level])))))
  ([chan]
   (boolean (some true? (flatten (map (fn [[language skill-level-map]]
                                        (map (fn [[skill-level _]]
                                               (queued? language skill-level chan)) skill-level-map)) @queues))))))

(defn add! [language skill-level chan]
  (if-not (some #{language} languages)
    (async/send! chan (t/write {:error "Invalid language"}))
    (if-not (some #{skill-level} skill-levels)
      (async/send! chan (t/write {:error "Invalid skill level"}))
      (if-not (queued? chan)
        (dosync
         (alter queues update-in [language skill-level] conj chan))
        (async/send! chan (t/write {:message "already queued"}))))))

(defn remove-from-all!
  "Removes the chan from all queues"
  [queues chan]
  (into {} (map (fn [[language skill-map]]
                  [language (into {} (map (fn [[skill-level queue]]
                                            [skill-level (into clojure.lang.PersistentQueue/EMPTY (remove #{chan} queue))]) skill-map))]) queues)))

(defn remove! 
  ([language skill-level chan]
   (dosync
    (alter queues update-in [language skill-level] #(into clojure.lang.PersistentQueue/EMPTY (remove #{chan} %)))))
  ([chan]
   (dosync
    (alter queues remove-from-all! chan))))

(defn match-and-notify! [queues-ref language skill-level]
  (let [start-work  (ref false)
        room-uuid   (str (java.util.UUID/randomUUID))
        match-ref   (ref nil)]
    (dosync
     (let [queue (-> @queues-ref
                     language
                     skill-level)
           match (take 2 queue)]
       (when (= 2 (count match))
         (ref-set match-ref match)
         (alter queues update-in [language skill-level] #(into clojure.lang.PersistentQueue/EMPTY (drop 2 %)))
         (alter rooms/rooms rooms/create room-uuid language skill-level match)
         (ref-set start-work true))))
    (when @start-work
      (io!
       (async/send! (first @match-ref) (t/write {:type      :match-found
                                                 :caller?   true
                                                 :room-uuid room-uuid}))
       (async/send! (second @match-ref) (t/write {:type      :match-found
                                                  :caller?   false
                                                  :room-uuid room-uuid}))))))
