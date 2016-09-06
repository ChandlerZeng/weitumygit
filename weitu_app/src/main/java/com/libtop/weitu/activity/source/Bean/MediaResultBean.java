package com.libtop.weitu.activity.source.Bean;

import java.util.List;


/**
 * Created by LianTu on 2016/7/22.
 */
public class MediaResultBean
{
    /**
     * code : 1
     * mediaAlbum : {"id":"5719c912984ea3435043ce36","lid":"5673c5f8984e4ce95056db32","state":30,"title":"12123123123","cover":"http://image.bookus.cn/cover/709/841/5719c912984ea3435043ce36_440X248.jpg","type":1,"artist":"","channel":0,"label1":270000,"label2":270100,"label3":0,"timeline":1464242000162,"hot":0,"comment":32,"view":22,"favorite":0,"open":1,"uploadUid":"56f30d8b984e85df349c37ef","uploadUsername":"18664837581","tags":[]}
     * mediaList : []
     * favorite : 0
     */

    public int code;
    public int favorite;
    public List<MediaListItemBean> mediaList;
    public MediaAlbumBean mediaAlbum;
}
