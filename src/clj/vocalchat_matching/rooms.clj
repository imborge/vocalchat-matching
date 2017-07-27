(ns vocalchat-matching.rooms)

(def rooms (ref {}))

(comment "Rooms example"
         {"2fcc4a-some-random-uuid" {:users       {:caller chan1
                                                   :callee chan2}
                                     :uuid        "2fcc4a-some-random-uuid"
                                     :language    :english
                                     :skill-level :beginner}})

(defn create [rooms room-uuid language skill-level match]
  (assoc rooms room-uuid {:users       {:caller (first match)
                                        :callee (second match)}
                          :uuid        room-uuid
                          :language    language
                          :skill-level skill-level}))

(defn remove! [room-uuid]
  (dosync
   (alter rooms dissoc room-uuid)))
