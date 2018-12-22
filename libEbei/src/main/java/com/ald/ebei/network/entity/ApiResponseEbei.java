package com.ald.ebei.network.entity;

/*
 * Created by liangchen on 2018/5/25.
 */


import com.ald.ebei.util.EbeiMiscUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

public class ApiResponseEbei {

//    西瓜信用返回格式
//    {
//        "data": {},
//        "message": "请求成功",
//            "status":0000
//    }

    private String msg;
    private int code;            //result对象中的code字段
    private JSONObject data;      //result对象中的JSONObject对象，注意：不是JSONArray对象


    public ApiResponseEbei(JSONObject jsonObject) {
        this.msg = jsonObject.getString("msg");
        this.code = jsonObject.getInteger("code");
        this.data = jsonObject.getJSONObject("data");
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int status) {
        this.code = status;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public <T> T getData(Class<T> cls) {
        return JSON.parseObject(data.toJSONString(), cls);
    }

    public <T> T getData(Type cls) {
        if (data != null && EbeiMiscUtils.isNotEmpty(data.toJSONString())) {
            return JSON.parseObject(data.toJSONString(), cls);
        } else {
            return null;
        }

    }
}
