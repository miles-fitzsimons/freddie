(ns freddie.core
  (:require
    [clj-http.client :as c]))


(def auth-token "")
(def artist-id "")
(def playlist-id "")
(def options {:headers {:Authorization (str "Bearer " auth-token)}
              :as      :json})

(defn get-related-artists
  []
  (->> (c/get
         (str "https://api.spotify.com/v1/artists/"
              artist-id
              "/related-artists")
         options)
       :body
       :artists
       (map :id)))


(defn get-artist-songs
  [id]
  (->> (c/get
         (str "https://api.spotify.com/v1/artists/"
              id
              "/top-tracks?country=US")
         options)
       :body
       :tracks
       (map :id)))


(defn get-all-related-songs
  []
  (-> (for [id (get-related-artists)]
        (get-artist-songs id))
      flatten
      shuffle))


(defn update-playlist
  []
  (for [song-id (get-all-related-songs)]
    (c/post
      (str "https://api.spotify.com/v1/playlists/"
           playlist-id
           "/tracks?uris=spotify:track:"
           song-id)
      options)))
