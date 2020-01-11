(ns freddie.core
  (:require
    [clj-http.client :as client]))


(def auth-token "fake-news")
(def artist-id "0Y4inQK6OespitzD6ijMwb")
(def playlist-id "you-can-find-it")


(defn get-related-artists []
  (->> (client/get
         (str "https://api.spotify.com/v1/artists/"
              artist-id
              "/related-artists")
         {:headers {:Authorization (str "Bearer " auth-token)}
          :as      :json})
       :body
       :artists
       (map #(:id %))))


(defn get-related-artist-songs
  [id]
  (->> (client/get
         (str "https://api.spotify.com/v1/artists/"
              id
              "/top-tracks?country=US")
         {:headers {:Authorization (str "Bearer " auth-token)}
          :as      :json})
       :body
       :tracks
       (map #(:id %))))


(defn get-all-related-songs
  []
  (flatten (for [id (get-related-artists)]
             (get-related-artist-songs id))))


(defn update-playlist
  []
  (for [song-id (get-all-related-songs)]
    (client/post
      (str "https://api.spotify.com/v1/playlists/"
           playlist-id
           "/tracks?uris="
           (str "spotify:track:" song-id))
      {:headers {:Authorization (str "Bearer " auth-token)}
       :as      :json})))
