package com.libtop.weituR.http.API;

import com.libtop.weituR.activity.classify.bean.ClassifyBean;
import com.libtop.weituR.activity.classify.bean.ClassifyDetailBean;
import com.libtop.weituR.activity.main.clickHistory.ResultBean;
import com.libtop.weituR.activity.main.dto.DocBean;
import com.libtop.weituR.activity.main.dto.ImageSliderDto;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.activity.search.dto.CommentResult;
import com.libtop.weituR.activity.source.Bean.MediaResultBean;
import com.libtop.weituR.activity.user.dto.CollectBean;
import com.libtop.weituR.dao.ResultCodeDto;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by LianTu on 2016/7/6.
 */
public interface WeituApi {

    //http://weitu.bookm.cn/focus/list.json?text={"method":"focus.list"}

    @GET("{type}/{method}")
    Observable<List<CollectBean>> getCollect(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<CommentResult>> getComment(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<ResultCodeDto> getResultCode(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<ResultBean>> getHistory(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<ClassifyBean>> getClassify(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<ClassifyDetailBean> getClassifyDetail(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<MediaResultBean> getMedia(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<DocBean>> getNewest(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<ImageSliderDto>> getImageSlider(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );

    @GET("{type}/{method}")
    Observable<List<NoticeInfo>> getNoticeInfo(
            @Path("type") String type,
            @Path("method") String method,
            @Query("text") String requestJson
    );
}
