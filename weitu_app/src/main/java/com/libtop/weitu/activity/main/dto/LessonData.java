package com.libtop.weitu.activity.main.dto;

import java.util.List;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class LessonData {
    private String id;
    private String title;
    private List<Item> mediaAlbums;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getMediaAlbums() {
        return mediaAlbums;
    }

    public void setMediaAlbums(List<Item> mediaAlbums) {
        this.mediaAlbums = mediaAlbums;
    }

    public class Item{
        private int favorite;
        private int hot;
        private int view;
        private String cover;
        private String id;
        private String title;
        private String artist;
        private String uploadUsername;
        private int type;//1视频呢;2音频

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getUploadUsername() {
            return uploadUsername;
        }

        public void setUploadUsername(String uploadUsername) {
            this.uploadUsername = uploadUsername;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public int getView() {
            return view;
        }

        public void setView(int view) {
            this.view = view;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }
    }
}
