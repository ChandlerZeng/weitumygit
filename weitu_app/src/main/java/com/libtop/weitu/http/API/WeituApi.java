package com.libtop.weitu.http.API;

import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.classify.bean.ClassifyDetailBean;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.dto.ImageSliderDto;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.activity.source.Bean.MediaResultBean;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.dao.ResultCodeDto;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by LianTu on 2016/7/6.
 */
public interface WeituApi
{

    //http://weitu.bookm.cn/focus/list.json?text={"method":"focus.list"}

    @GET("{type}/{method}")
    Observable<List<CollectBean>> getCollect(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<CommentResult>> getComment(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<ResultCodeDto> getResultCode(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<ResultBean>> getHistory(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<ClassifyBean>> getClassify(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<ClassifyDetailBean> getClassifyDetail(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<MediaResultBean> getMedia(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<DocBean>> getNewest(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<ImageSliderDto>> getImageSlider(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @GET("{type}/{method}")
    Observable<List<BookDto>> getBookDto(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);

    @POST("{type}/{method}")
    Observable<List<SubjectBean>> getSubjectDto(@Path("type") String type, @Path("method") String method, @Query("text") String requestJson);
}
