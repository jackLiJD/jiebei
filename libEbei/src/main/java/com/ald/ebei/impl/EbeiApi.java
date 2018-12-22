package com.ald.ebei.impl;

import com.ald.ebei.auth.EbeiIDCardInfoModel;
import com.ald.ebei.auth.model.EbeiFaceLivenessModel;
import com.ald.ebei.auth.model.EbeiIDCardModel;
import com.ald.ebei.auth.model.EbeiPhoneOperatorModel;
import com.ald.ebei.auth.model.EbeiUploadCardResultModel;
import com.ald.ebei.dushi.model.EbeiBankCardListModel;
import com.ald.ebei.dushi.model.EbeiHomeModel;
import com.ald.ebei.dushi.model.EbeiProtocolListModel;
import com.ald.ebei.dushi.model.EbeiRepaymentSuccessModel;
import com.ald.ebei.dushi.model.EbeiSupportBankListModel;
import com.ald.ebei.dushi.model.EbeiUserInfoModel;
import com.ald.ebei.model.EbeiAvailableCreditModel;
import com.ald.ebei.model.EbeiHelpDetaildsModel;
import com.ald.ebei.model.EbeiLandCheckModel;
import com.ald.ebei.model.EbeiLoginModel;
import com.ald.ebei.model.EbeiUrlModel;
import com.ald.ebei.network.entity.EbeiApiResponse;
import com.ald.ebei.network.entity.ApiResponseEbei;
import com.ald.ebei.dushi.model.EbeiAnalyzePeriodsModel;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface EbeiApi {

    /**
     * @param url
     * @return
     */
    @POST
    Call<JSONObject> forwardInfo(@Url String url, @Body JSONObject reqObj);

    /**
     * 都市E贷接口-----------------------------------------------------------都市E贷接口
     */
    //获取首页可贷金额
    @POST("sdk/hostUser/getAvailableCredit")
    Observable<EbeiAvailableCreditModel> getAvailableCredit(@Body JSONObject requestObj);

    //获取帮助中心详情
    @POST("sdk/helpCenter/getInfo")
    Observable<EbeiHelpDetaildsModel> helpCenterDetails(@Body JSONObject requestObj);

    @Multipart
    @POST
    Call<EbeiUrlModel> uploadImage(@Url String url, @Part List<MultipartBody.Part> partList);


    /**
     * 上传头像
     */
    @Multipart
    @POST
    Observable<EbeiUploadCardResultModel> uploadFile(@Url String url, @Part List<MultipartBody.Part> partList);

    /**
     * 上传头像
     */
    @Multipart
    @POST
    Call<EbeiIDCardInfoModel> uploadIDFile(@Url String url, @Part List<MultipartBody.Part> partList);

    /**
     * face++ 提交身份证
     */
    @POST("/api/user/auth/saveRealnameInfo")
    Observable<EbeiIDCardModel> saveRealnameInfo();

    @Multipart
    @POST
    Observable<EbeiFaceLivenessModel> uploadFaceLivenessFile(@Url String url, @Part List<MultipartBody.Part> partList);

    /**
     * 经纬度转换城市
     */
    @POST("regeo")
    Call<ResponseBody> conversionLatLon(@Query("location") String location,
                                        @Query("output") String output,
                                        @Query("key") String key,
                                        @Query("radius") String radius,
                                        @Query("extensions") String extensions);

    /**
     * 获取手机运营商url
     */
    @POST("/app/open/sdk/authentication/authMobile")
    Call<EbeiPhoneOperatorModel> authMobile();

    /**
     * 公用认证接口
     * 网银 ONBK ,学信 CHSI ,公积金 FUND ,信用卡 CRED ,支付宝 ALIP ,社保SOCI
     *
     * @param reqObj reqBody
     * @return
     */
    @POST("/app/open/sdk/authentication/authAuth")
    Observable<JSONObject> authAuth(@Body JSONObject reqObj);

    /**
     * 注册接口
     */
    @POST("api/nnl/register/submit")
    Observable<JSONObject> userRegister(@Body JSONObject jsonObject);

    /**
     * 登录
     */
    @POST("api/nnl/login/inByPwd")
    Observable<EbeiLoginModel> userLogin(@Body JSONObject jsonObject);

    /**
     * 获取短信验证码
     */
    @POST("api/nnl/sms/sendCode")
    Observable<JSONObject> requestSendMsgCode(@Body JSONObject jsonObject);

    /**
     * 找回密码第一步校验验证码
     */
    @POST("api/nnl/login/forget/CheckCode")
    Observable<JSONObject> checkFindPwdCode(@Body JSONObject jsonObject);

    /**
     * 设置新密码并登录
     */
    @POST("api/nnl/login/forget/Submit")
    Observable<EbeiLoginModel> setNewPwdAndLogin(@Body JSONObject jsonObject);

    /**
     * 未登录首页
     *
     * @return
     */
    @POST("/api/nnl/homepage")
    Observable<EbeiHomeModel> unSignInhomepage();

    /**
     * 已登录首页
     *
     * @return
     */
    @POST("/api/homepage")
    Observable<EbeiHomeModel> signInhomepage();

    //绑卡相关

    /**
     * 添加银行卡
     *
     * @param reqObj
     * @return
     */
    @POST("/api/bankUser/add")
    Observable<EbeiApiResponse> bankCardAdd(@Body JSONObject reqObj);

    /**
     * 签约(获取验证码)
     *
     * @param reqObj
     * @return
     */
    @POST("/api/bankUser/authSign")
    Observable<EbeiApiResponse> authSign(@Body JSONObject reqObj);

    /**
     * 删除银行卡
     *
     * @param reqObj
     * @return
     */
    @POST("/api/bankUser/delete")
    Observable<EbeiApiResponse> delete(@Body JSONObject reqObj);

    /**
     * 查询银行卡详情
     *
     * @param reqObj
     * @return
     */
    @POST("/api/bankUser/getById")
    Call<EbeiApiResponse> getById(@Body JSONObject reqObj);

    /**
     * 查询绑定的银行卡
     *
     * @return
     */
    @POST("/api/bankUser/list")
    Observable<EbeiBankCardListModel> getBindBankList();

    /**
     * 查询所属银行列表(支持的银行卡)
     *
     * @return
     */
    @POST("/api/bankUser/listBank")
    Observable<EbeiSupportBankListModel> listBank();

    /**
     * 设为主卡
     *
     * @param reqObj
     * @return
     */
    @POST("/api/bankUser/setMain")
    Observable<EbeiApiResponse> setMain(@Body JSONObject reqObj);

    /**
     * 退出登录
     */
    @POST("api/nnl/login/out")
    Observable<JSONObject> logout();

    /**
     * 重新设置密码校验验证码
     */
    @POST("api/user/resetPwd/CheckCode")
    Observable<JSONObject> checkResetPwdMsgCode(@Body JSONObject jsonObject);

    /**
     * 重新设置密码
     */
    @POST("api/user/resetPwd/Submit")
    Observable<JSONObject> resetPwd(@Body JSONObject JSONObject);

    /**
     * 验证码登录
     */
    @POST("api/nnl/login/inBySms")
    Observable<EbeiLoginModel> userLoginByCode(@Body JSONObject jsonObject);

    /**
     * 获取月供和手续费
     */
    @POST("api/analyzePeriods")
    Observable<EbeiAnalyzePeriodsModel> getAnalyzePeriods(@Body JSONObject jsonObject);

    /**
     * 手动修改证件名
     */
    @POST("/api/user/auth/changeRealname")
    Observable<EbeiApiResponse> changeRealname(@Body JSONObject object);

    /**
     * 申请借款
     */
    @POST("api/loan/applyLoan")
    Observable<JSONObject> applyLoan(@Body JSONObject jsonObject);

    /**
     * 用户状态检查
     */
    @POST("api/user/auth/check")
    Observable<EbeiLandCheckModel> userLandCheck();

    /**
     * 获取用户基本信息
     *
     * @return
     */
    @POST("/api/user/getUserInfo")
    Observable<EbeiUserInfoModel> getUserInfo();

    /**
     * 用户已完成三方认证
     *
     * @param reqObj
     * @return
     */
    @POST("/api/user/auth/thirdFinished")
    Observable<ApiResponseEbei> thirdFinished(@Body JSONObject reqObj);

    /**
     * 还款
     *
     * @param reqObj
     * @return
     */
    @POST("/api/repay/applyRepay")
    Observable<EbeiRepaymentSuccessModel> applyRepay(@Body JSONObject reqObj);

    /**
     * 获取协议列表
     *
     * @return
     */
    @POST("/api/protocol/getProtocolList")
    Observable<EbeiProtocolListModel> getProtocolList(@Body JSONObject reqObj);

    /**
     * 定位认证
     *
     * @param reqObj
     * @return
     */
    @POST("/api/user/auth/location")
    Observable<JSONObject> location(@Body JSONObject reqObj);

    /**
     * 定位认证
     *
     * @param reqObj
     * @return
     */
    @POST("/api/user/auth/contacts")
    Observable<JSONObject> contacts(@Body JSONObject reqObj);

    /**
     * 钱包登录
     * @param reqObj
     * @return
     */
    @POST("/api/nnl/login/inForEds")
    Observable<EbeiLoginModel> inForEds(@Body JSONObject reqObj);
}
