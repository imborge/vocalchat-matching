(ns vocalchat-matching.transit
  (:require [cognitect.transit :as t])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))

(def string-encoding "UTF-8")

(defn read [s]
  (let [in (ByteArrayInputStream. (.getBytes s string-encoding))
        reader (t/reader in :json)]
    (t/read reader)))

(defn write [o]
  (let [out (ByteArrayOutputStream. 4096)
        writer (t/writer out :json)]
    (t/write writer o)
    (.toString out string-encoding)))
