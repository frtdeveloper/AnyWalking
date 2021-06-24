package com.lenwotion.travel.interfaces;

import com.lenwotion.travel.bean.search.SearchLineResponseBean;
import com.lenwotion.travel.bean.search.SearchLineDirectionResponseBean;
import com.lenwotion.travel.bean.search.SearchStationResponseBean;
import com.lenwotion.travel.global.GlobalConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 查询模块
 * Created by fq on 2017/11/24.
 */
public interface ISearchService {

    /**
     * 站台模糊匹配查询
     */
    @FormUrlEncoded
    @POST(GlobalConstants.SEARCH_STATION_MATCH)
    Call<SearchStationResponseBean> getSearchStationMatchData(@Field("userToken") String userToken,
                                                              @Field("filed") String filed,
                                                              @Field("city") String city);

    /**
     * 线路模糊匹配查询
     */
    @FormUrlEncoded
    @POST(GlobalConstants.SEARCH_LINE_MATCH)
    Call<SearchLineResponseBean> getSearchLineMatchData(@Field("userToken") String userToken,
                                                        @Field("filed") String filed,
                                                        @Field("city") String city);

    /**
     * 精准线路上下行查询
     */
    @FormUrlEncoded
    @POST(GlobalConstants.SEARCH_LINE_DIRECTION)
    Call<SearchLineDirectionResponseBean> getSearchLineDirectionData(@Field("userToken") String userToken,
                                                                     @Field("line") String line,
                                                                     @Field("city") String city);

//    /**
//     * 精品软件推荐
//     */
//    @POST(GlobalConstant.SEARCH_OBTAIN_SOFTWARE)
//    Call<RecommendBean> getReminderSoftwareData();

}
