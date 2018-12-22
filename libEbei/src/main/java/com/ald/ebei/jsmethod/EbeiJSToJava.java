package com.ald.ebei.jsmethod;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.ald.ebei.EbeiApplication;
import com.ald.ebei.EbeiHtml5WebView;
import com.ald.ebei.R;
import com.ald.ebei.auth.EbeiMxAuthCallBack;
import com.ald.ebei.auth.activity.EbeiIdAuthActivity;
import com.ald.ebei.auth.model.EbeiPhoneOperatorModel;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.dushi.activity.EbeiBankCardAddActivity;
import com.ald.ebei.dushi.activity.EbeiDsMainActivity;
import com.ald.ebei.dushi.model.EbeiUserInfoModel;
import com.ald.ebei.impl.EbeiApi;
import com.ald.ebei.network.EbeiBaseObserver;
import com.ald.ebei.network.EbeiClient;
import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.network.EbeiRequestCallBack;
import com.ald.ebei.network.entity.ApiResponseEbei;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.util.EbeiBundleKeys;
import com.ald.ebei.util.EbeiConstant;
import com.ald.ebei.util.EbeiActivityUtils;
import com.ald.ebei.util.EbeiInfoUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.ald.ebei.util.EbeiMXAuthUtils;
import com.ald.ebei.util.EbeiPermissionCheck;
import com.ald.ebei.util.EbeiPermissions;
import com.ald.ebei.util.EbeiRxUtils;
import com.ald.ebei.util.EbeiUIUtils;
import com.ald.ebei.util.baiduloc.EbeiLocationService;
import com.ald.ebei.util.log.EbeiLogger;
import com.alibaba.fastjson.JSONObject;
import com.ald.ebei.util.EbeiContactsUtils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.moxie.client.model.MxParam;

import retrofit2.Call;
import retrofit2.Response;

import static com.ald.ebei.util.EbeiPermissionCheck.REQUEST_CODE_CONTACT;

/**
 * Js交互
 * Created by sean yu on 2017/5/23.
 */
public class EbeiJSToJava {
    private static final String TAG = "EbeiJSToJava";
    private WebView webView;
    private EbeiMXAuthUtils authUtils;
    private final int REQUEST_CODE_REFRESH = 0x1000;

    public EbeiJSToJava(WebView webView) {
        this.webView = webView;
    }


    @JavascriptInterface
    public void closeWebView() {
        if (webView.getContext() instanceof Activity) {
            ((Activity) webView.getContext()).finish();
        }
    }

    @JavascriptInterface
    public void openActivity(String openInfo) {
        JSONObject object = JSONObject.parseObject(openInfo);
        String className = object.getString("className");
        try {
            Class openClass = Class.forName(className);
            EbeiActivityUtils.push(openClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    private void loadUrl(final String url) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    /**
     * 获取设备信息
     */
    @JavascriptInterface
    public void getDeviceInfo() {
        try {
            PackageManager pm = EbeiConfig.getContext().getPackageManager();
            Context context = EbeiConfig.getContext();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceType", "android");
            jsonObject.put("deviceId", EbeiInfoUtils.getDeviceId());
            jsonObject.put("appVersion", pi.versionCode);
            jsonObject.put("osType", "android_" + Build.VERSION.RELEASE);
            jsonObject.put("phoneType", Build.MODEL);

            final String result = String.format(EbeiJavaToJS.RECEIVE_DEVICEINFO,
                    jsonObject.toJSONString());

            if (webView != null) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(result);
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * 获取地理位置
     */
    @JavascriptInterface
    public void getLocation() {
//        //定位权限
        EbeiPermissionCheck.getInstance().askForPermissions((Activity) webView.getContext(), EbeiPermissions.locationPermissions,
                EbeiPermissionCheck.REQUEST_CODE_ALL);

        // -----------location config ------------
        EbeiLocationService ebeiLocationService = ((EbeiApplication) ((Activity) webView.getContext()).getApplication()).ebeiLocationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        ebeiLocationService.registerListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                    ebeiLocationService.unregisterListener(this); //注销掉监听
                    ebeiLocationService.stop(); //停止定位服务
                    JSONObject locationJson = new JSONObject();
                    locationJson.put("latitude", bdLocation.getLatitude());
                    locationJson.put("longitude", bdLocation.getLongitude());
                    locationJson.put("address", bdLocation.getAddress().address);
                    locationAuth(locationJson);
//                    Log.d("province", "longitude:" + longitude + " city:" + city + " county:" + county + " address:" + address + "latitude:" + latitude + "province:" + province);
//                    final String result = String.format(EbeiJavaToJS.RECEIVE_LOCATION, locationJson.toJSONString());
//                    if (webView != null) {
//                        webView.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                webView.loadUrl(result);
//                            }
//                        });
//                    }
                } else {
                    EbeiUIUtils.showToast("未获取到定位，请开启权限后重试");
                }
            }
        });
        ebeiLocationService.start();

    }

    private void locationAuth(JSONObject location) {
        EbeiClient.getService(EbeiApi.class)
                .location(location)
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        if (webView != null) {
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(EbeiJavaToJS.RECEIVE_LOCATION_WITHOUT_PARAM);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * @param jsonParams {"type":"id","zmurl":"","moxieUserID":""}
     */
    @JavascriptInterface
    public void authenticate(String jsonParams) {
        EbeiLogger.d(TAG, "调用方法");
        if (authUtils == null) {
            initAuthUtils();
        }
        EbeiLogger.d(TAG, "authenticate" + "type");
        JSONObject jsonObject = JSONObject.parseObject(jsonParams);
        if (jsonObject == null)
            return;
        String type = jsonObject.get("type").toString();
        if ("id".equals(type)) {
            //身份证认证
            EbeiActivityUtils.push(EbeiIdAuthActivity.class);
        } else if ("ZM".equals(type)) {
            //芝麻认证
            String zmurl = JSONObject.parseObject(jsonParams).get("zmurl").toString();
            if (EbeiMiscUtils.isEmpty(zmurl)) {
                EbeiUIUtils.showToast(EbeiConfig.getContext().getResources().getString(R.string.ebei_credit_idf_toast));
            } else {
                handZmAuth(zmurl);
            }
        } else {
            //公用认证
            judgeAuthCommon(type);
        }
    }


    /**
     * 获取通讯录信息
     */
    @JavascriptInterface
    public void getContacts() {
        //通讯录权限
        Activity activity = EbeiConfig.getCurrentActivity();
        if (!EbeiPermissionCheck.getInstance().checkPermission(activity, Manifest.permission.READ_CONTACTS)) {
            EbeiPermissionCheck.getInstance().askForPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_CONTACT);
        } else {
            readContacts();
        }
    }

    private void readContacts() {
        try {
//            String result = String.format(EbeiJavaToJS.RECEIVE_CONTACTS, EbeiContactsUtils.getPostContacts(EbeiConfig.getCurrentActivity()));
            String result = EbeiContactsUtils.getPostContacts(EbeiConfig.getCurrentActivity());
//            loadUrl(result);
            contactsAuth(result);
        } catch (Exception ex) {
            //权限弹框
            EbeiPermissionCheck.getInstance().showContactAskDialog(EbeiConfig.getCurrentActivity(), R.string.ebei_permission_name_contacts);
        }
    }

    private void contactsAuth(String contacts) {
        JSONObject reqObj=new JSONObject();
        reqObj.put("contacts",contacts);
        EbeiClient.getService(EbeiApi.class)
                .contacts(reqObj)
                .compose(EbeiRxUtils.<JSONObject>io_main())
                .subscribe(new EbeiBaseObserver<JSONObject>() {
                    @Override
                    public void onNext(JSONObject jsonObject) {
                        super.onNext(jsonObject);
                        if (webView != null) {
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl(EbeiJavaToJS.RECEIVE_CONTACTS_WITHOUT_PARAM);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * 芝麻授权
     */
    private void handZmAuth(String url) {
        Intent i = new Intent();
        i.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, url);
        EbeiActivityUtils.push(EbeiHtml5WebView.class, i, REQUEST_CODE_REFRESH);
    }


    /**
     * 处理手机运营商逻辑
     */
    private void handleMobileOperator() {
        Call<EbeiPhoneOperatorModel> call = EbeiClient.getService(EbeiApi.class).authMobile();
        EbeiNetworkUtil.showCutscenes(EbeiConfig.getContext(), call);
        call.enqueue(new EbeiRequestCallBack<EbeiPhoneOperatorModel>() {
            @Override
            public void onSuccess(Call<EbeiPhoneOperatorModel> call, Response<EbeiPhoneOperatorModel> response) {
                if (response.body() != null) {
                    String url = response.body().getUrl();
                    if (EbeiMiscUtils.isNotEmpty(url)) {
                        Intent i = new Intent();
                        i.putExtra(EbeiHtml5WebView.INTENT_BASE_URL, url);
                        EbeiActivityUtils.push(EbeiHtml5WebView.class, i, REQUEST_CODE_REFRESH);
                    }
                } else {
                    EbeiUIUtils.showToast(EbeiConfig.getContext().getResources().getString(R.string.ebei_credit_promote_phone_err));
                }
            }
        });
    }

    /**
     * 网银 ONBK ,学信 CHSI ,公积金 FUND ,信用卡 CRED ,支付宝 ALIP ,社保SOCI
     *
     * @param authType 认证类型
     */
    private void judgeAuthCommon(final String authType) {
        EbeiClient.getService(EbeiApi.class)
                .getUserInfo()
                .compose(EbeiRxUtils.<EbeiUserInfoModel>io_main())
                .subscribe(new EbeiBaseObserver<EbeiUserInfoModel>() {
                    @Override
                    public void onNext(EbeiUserInfoModel ebeiUserInfoModel) {
                        super.onNext(ebeiUserInfoModel);
                        if (ebeiUserInfoModel != null) {
                            String realName = ebeiUserInfoModel.getRealName();
                            String idCard = ebeiUserInfoModel.getIdcard();
                            StringBuilder builder = new StringBuilder("fk_dseb");
                            builder.append(",")
                                    .append(authType)
                                    .append(",")
                                    .append(idCard);
                            if (EbeiMiscUtils.isNotEmpty(builder.toString())) {
                                String userId = builder.toString();
                                authUtils.configMxParams(userId);
                                if ("FUND".equals(authType)) {
                                    authUtils.handleFund();
                                } else if ("SOCI".equals(authType)) {
                                    authUtils.handleSecurity();
                                } else if ("ALIP".equals(authType)) {
                                    authUtils.handleAlipay();
                                } else if ("CRED".equals(authType)) {
                                    authUtils.handleOnlineBack();
                                } else if ("ONBK".equals(authType)) {
                                    authUtils.handleOnlineBank();
                                } else if ("CHSI".equals(authType)) {
                                    authUtils.handleChsi();
                                } else if ("MOBILE".equals(authType)) {
                                    authUtils.handleOperator(EbeiConfig.getEbeiAccountProvider().getUserName(), realName, idCard);
                                } else if ("zhengxin".equals(authType)) {
                                    authUtils.handleZhengxin();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }


    private void initAuthUtils() {
        authUtils = new EbeiMXAuthUtils((Activity) webView.getContext()).setCallBack(new EbeiMxAuthCallBack() {
            @Override
            public void authSuccess(final String authCode, String msg, final String userId) {
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject respObj = new JSONObject();
                        respObj.put("orderno", userId);
                        switch (authCode) {
                            case MxParam.PARAM_TASK_FUND:
                                thirdFinished("FUND");
                                break;

                            case MxParam.PARAM_TASK_ONLINEBANK:
                                thirdFinished("ONBK");
                                break;

                            case MxParam.PARAM_TASK_EMAIL:
                                break;

                            case MxParam.PARAM_TASK_SECURITY:
                                thirdFinished("SOCI");
                                break;

                            case MxParam.PARAM_TASK_ALIPAY:
                                break;
                            case MxParam.PARAM_TASK_CHSI:
                                break;
                            case MxParam.PARAM_TASK_ZHENGXIN:
                                break;
                            case MxParam.PARAM_TASK_CARRIER:
                                thirdFinished("MOBILE");
                        }
                        webView.loadUrl(String.format(EbeiJavaToJS.KITKAT_JS.RECEIVE_AUTH_FINISH, respObj.toJSONString()));
                    }
                });
            }

            @Override
            public void authError(String authCode, String errorMsg) {
                EbeiUIUtils.showToast(errorMsg);
            }
        });
    }

    private void thirdFinished(String type) {
        JSONObject reqObj = new JSONObject();
        reqObj.put("authType", type);
        EbeiClient.getService(EbeiApi.class)
                .thirdFinished(reqObj)
                .compose(EbeiRxUtils.<ApiResponseEbei>io_main())
                .subscribe(new EbeiBaseObserver<ApiResponseEbei>() {
                    @Override
                    public void onNext(ApiResponseEbei apiResponseEbei) {
                        super.onNext(apiResponseEbei);
                    }

                    @Override
                    public void onFailure(EbeiApiException ebeiApiException) {
                        super.onFailure(ebeiApiException);
                        EbeiUIUtils.showToast(ebeiApiException.getMsg());
                    }
                });
    }

    /**
     * Forward
     *
     * @param info info
     */
    @JavascriptInterface
    public void getForwardInfo(String info) {
        EbeiLogger.d(TAG, info);
        if (EbeiMiscUtils.isNotEmpty(info)) {
            JSONObject getObj = JSONObject.parseObject(info);
            if (getObj != null) {
                String url = getObj.getString("url");
                String param = getObj.getString("params");
                final String callbacl = getObj.getString("callback");
                JSONObject reqObj = new JSONObject();
                if (EbeiMiscUtils.isNotEmpty(param)) {
                    try {
                        reqObj = JSONObject.parseObject(param);
                    } catch (Exception e) {
                        reqObj = new JSONObject();
                    }
                }
                Call<JSONObject> call = EbeiClient.getService(EbeiApi.class).forwardInfo(url, reqObj);
                call.enqueue(new EbeiRequestCallBack<JSONObject>() {
                    @Override
                    public void onSuccess(Call<JSONObject> call, Response<JSONObject> response) {
                        EbeiLogger.d(TAG, "ForwardResponseData_" + response.body().toJSONString());
                        if (response.body() != null) {
                            JSONObject respObj = new JSONObject();
                            respObj.put("code", "1000");
                            respObj.put("data", response.body());
                            final String reslutNew = "javascript:" + callbacl + "('" + respObj.toJSONString() + "')";
                            EbeiLogger.d(TAG, "ForwardResponseData_NEW" + reslutNew);

                            if (webView != null) {
                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.loadUrl(reslutNew);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        super.onFailure(call, t);
                        EbeiLogger.e(TAG, "ForwardFailed");
                        if (t instanceof EbeiApiException) {
                            EbeiApiException ebeiApiException = (EbeiApiException) t;
                            JSONObject respObj = new JSONObject();
                            respObj.put("code", ebeiApiException.getCode());
                            respObj.put("data", "");
                            final String reslutNew = "javascript:" + callbacl + "('" + respObj.toJSONString() + "')";

                            if (webView != null) {
                                webView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.loadUrl(reslutNew);
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    @JavascriptInterface
    public void closeWebPage() {
        if (webView.getContext() != null) {
            EbeiActivityUtils.pop();
        }
    }

    public void onRequestPermissionsResult(final Activity activity, final int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //请求通讯录权限
            if (requestCode == REQUEST_CODE_CONTACT) {
                readContacts();
            }
            return;
        }
        // 权限被用户拒绝时被调用
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) { // 用户拒绝权限但未勾选不再询问
            if (requestCode == REQUEST_CODE_CONTACT) {
                showH5Dialog(requestCode);
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("权限提示")
                        .setMessage("该功能需要权限支持,请点击允许")
                        .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                            }
                        })
                        .setNegativeButton("拒绝", null)
                        .create().show();
            }
        } else { // 用户拒绝权限并勾选不再询问
            if (requestCode == REQUEST_CODE_CONTACT) {
                showH5Dialog(requestCode);
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("权限提示")
                        .setMessage("该功能所需权限被拒绝,请在手机设置中打开应用所需相机权限")
                        .setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("不去", null)
                        .create().show();
            }
        }
    }

    private void showH5Dialog(int requestCode) {
        if (requestCode == REQUEST_CODE_CONTACT) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:showAskContactsPanel()");
                }
            });
        }
    }

    @JavascriptInterface
    public void setPermissions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", webView.getContext().getPackageName(), null);
        intent.setData(uri);
        webView.getContext().startActivity(intent);
    }

    @JavascriptInterface
    public void appLogin() {
//        EbeiActivityUtils.finishAll();
//        EbeiActivityUtils.push(LoginActivity.class);
        EbeiConfig.getLocalBroadcastManager().sendBroadcast(new Intent(EbeiConstant.BROADCAST_ACTION_LOGIN));
    }

    /**
     * 添加银行卡
     */
    @JavascriptInterface
    public void toAddBankCard() {
        EbeiActivityUtils.push(EbeiBankCardAddActivity.class);
    }

    /**
     * 回到首页
     */
    @JavascriptInterface
    public void returnHomePage(String s) {
        Intent i = new Intent();
        i.putExtra(EbeiBundleKeys.MAIN_DATA_TAB, 0);
        EbeiActivityUtils.push(EbeiDsMainActivity.class, i);
        EbeiActivityUtils.pop();
    }

    /**
     * 借款列表还款
     */
    @JavascriptInterface
    public void toRepay(String s) {
        Intent i = new Intent();
        i.putExtra(EbeiBundleKeys.MAIN_DATA_TAB, 0);
        EbeiActivityUtils.push(EbeiDsMainActivity.class, i);
        EbeiActivityUtils.pop();
    }

    @JavascriptInterface
    public void copyUrl(String s) {
        ClipboardManager clipboardManager = (ClipboardManager) webView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy", s);
        clipboardManager.setPrimaryClip(clipData);
        EbeiUIUtils.showToastWithoutTime("复制成功");
    }

    @JavascriptInterface
    public void openAliPay(String s) {
        try {
            PackageManager packageManager = EbeiConfig.getContext().getPackageManager();
            Intent i = packageManager.getLaunchIntentForPackage("com.eg.android.AlipayGphone");
            Activity act = EbeiActivityUtils.peek();
            if (null != act) act.startActivity(i);
        } catch (Exception e) {
            EbeiUIUtils.showToastWithoutTime("请先下载支付宝app");
        }
    }

}
