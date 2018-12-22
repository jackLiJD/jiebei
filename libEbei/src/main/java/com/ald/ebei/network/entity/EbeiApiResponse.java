package com.ald.ebei.network.entity;

import com.ald.ebei.util.EbeiMiscUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacky Yu on 2016/7/8.
 * 所有网络请求返回Json对象，必须符合以下模板（与服务端约定），否则出错
 * {
 * "id": "26",
 * "result": {
 * "msg": "success"
 * "code": 1000,
 * "data": {
 * "sign": "AX5234Bl243Bwde ",
 * "ip": "201.123.202.34",
 * "isFirst": "1",
 * "lastLoginTime": " 1280977330000 "
 * "info": {
 * "nickName": "啊啦",
 * "gender": "1",
 * "iconUrl": "https://xxx.xxx.com/xxx.jpg",
 * "city": "HZ",
 * "birthady": "1988-02-28",
 * "constelation": "双鱼",
 * …..
 * ｝,
 * },
 * }
 * }
 * 或者
 * {
 * "id": "26",
 * "result": {
 * "msg": "success"
 * "code": 1000,
 * "data": {
 * "arrayListName": [
 * {
 * "nickName": "啊啦1",
 * "gender": "1",
 * "iconUrl": "https://xxx.xxx.com/xxx1.jpg",
 * ｝,
 * {
 * "nickName": "啊啦2",
 * "gender": "2",
 * "iconUrl": "https://xxx.xxx.com/xxx2.jpg",
 * ｝,
 * {
 * "nickName": "啊啦3",
 * "gender": "3",
 * "iconUrl": "https://xxx.xxx.com/xxx3.jpg",
 * ｝,
 * ............
 * ],
 * },
 * }
 * <p/>
 * 或者分页返回结构体
 * {
 * "id": "26",
 * "result": {
 * "msg": "success"
 * "code": 1000,
 * "data": {
 * totalPage:3;
 * currentPage:1;
 * "messageList": [
 * {
 * "nickName": "啊啦1",
 * "gender": "1",
 * "iconUrl": "https://xxx.xxx.com/xxx1.jpg",
 * ｝,
 * {
 * "nickName": "啊啦2",
 * "gender": "2",
 * "iconUrl": "https://xxx.xxx.com/xxx2.jpg",
 * ｝,
 * ............
 * ],
 * },
 * }
 * }
 * 或者分页返回结构体
 * {
 * "id": "26",
 * "result": {
 * "msg": "success"
 * "code": 1000,
 * "data": {
 * info:{
 * totalPage:3;
 * currentPage:1;
 * "messageList": [
 * {
 * "nickName": "啊啦1",
 * "gender": "1",
 * "iconUrl": "https://xxx.xxx.com/xxx1.jpg",
 * ｝,
 * {
 * "nickName": "啊啦2",
 * "gender": "2",
 * "iconUrl": "https://xxx.xxx.com/xxx2.jpg",
 * ｝,
 * ............
 * ],
 * }
 * "ip": "201.123.202.34",
 * "isFirst": "1",
 * "lastLoginTime": " 1280977330000 "
 * },
 * }
 * <p/>
 * <p/>
 * 失败：
 * {
 * "id": "26",
 * "result": {
 * "msg": "false",
 * "code": 1001
 * }
 * }
 */

public class EbeiApiResponse {
    private JSONObject jsonObject;//返回的所有JSONObject对象
    private String id;            //某一个请求返回的id
    private JSONObject result;    //某一个请求返回的JSONObject类型的result对象
    private String msg;           //result对象中的msg消息字段
    private int code;            //result对象中的code字段
    private JSONObject data;      //result对象中的JSONObject对象，注意：不是JSONArray对象

    public EbeiApiResponse() {
    }

    public EbeiApiResponse(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.id = jsonObject.getString("id");
        this.result = jsonObject.getJSONObject("result");
        if (result == null) {
            return;
        }
        if (result.containsKey("msg")) {
            this.msg = result.getString("msg");
        }
        if (result.containsKey("code")) {
            this.code = result.getIntValue("code");
        }
        if (result.containsKey("data")) {
            this.data = result.getJSONObject("data");
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public JSONObject getResult() {
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setCode(int code) {
        this.code = code;
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
        if(data != null && EbeiMiscUtils.isNotEmpty(data.toJSONString())){
            return JSON.parseObject(data.toJSONString(), cls);
        }else {
            return null;
        }

    }
    /**
     * 获取data中的对象
     *
     * @param entity 对象名
     */
    public <T> T getData(String entity, Class<T> cls) {
        String[] ss = entity.split("\\.");
        JSONObject json = data;
        for (String s : ss) {
            json = json.getJSONObject(s);
        }
        return JSON.parseObject(json.toJSONString(), cls);
//        return JSON.toJavaObject(json, cls);
    }

    /**
     * 获取data中的分页对象
     *
     * @param entity 对象名
     */
    public <T> EbeiApiPageData<T> getPageData(String entity, Class<T> cls) {
        return parsePageResponse(data, entity, cls);

    }

    private <T> EbeiApiPageData<T> parsePageResponse(JSONObject json, String entity, Class<T> cls) {
        EbeiApiPageData<T> ebeiApiPageData = new EbeiApiPageData<T>();
        ebeiApiPageData.setPageNo(json.getIntValue("pageNo"));
        if (json.containsKey("totalPage")) {
            ebeiApiPageData.setTotalPage(json.getIntValue("totalPage"));
        }
        if (json.containsKey("nextTimestamp")) {
            ebeiApiPageData.setNextTimestamp(json.getInteger("nextTimestamp"));
        }
        List<T> list = getApiPageDataArray(json,entity, cls);
        ebeiApiPageData.setDataList(list);
        return ebeiApiPageData;
    }

    /**
     * 获取data中的分页对象
     *
     * @param cls 类
     */
    public <T> EbeiApiPageData<T> getPageData(Class<T> cls) {
        return parsePageResponse(data, cls);
    }

    private <T> EbeiApiPageData<T> parsePageResponse(JSONObject json, Class<T> cls) {
        EbeiApiPageData<T> ebeiApiPageData = new EbeiApiPageData<T>();
        ebeiApiPageData.setPageNo(json.getIntValue("pageNo"));
        if (json.containsKey("totalPage")) {
            ebeiApiPageData.setTotalPage(json.getIntValue("totalPage"));
        }
        if (json.containsKey("nextTimestamp")) {
            ebeiApiPageData.setNextTimestamp(json.getInteger("nextTimestamp"));
        }
        List<T> list = getApiPageDataArray(json, cls);
        ebeiApiPageData.setDataList(list);
        return ebeiApiPageData;
    }

    /**
     * 获取data中的对象
     *
     * @param entity 对象名，以 . 分割开
     */
    public <T> T getEntity(String entity, Class<T> cls) {
        String[] ss = entity.split("\\.");
        JSONObject json = data;
        for (String s : ss) {
            json = json.getJSONObject(s);
        }
        return JSON.toJavaObject(json, cls);
    }

    /**
     * 获取data中的list对象
     */
    public <T> List<T> getDataArray(Class<T> cls) {
        return getDataArray("messageList", cls);
    }

    /**
     * 获取data中的list对象
     *
     * @param entity list对象名，以 . 分割开
     */
    public <T> List<T> getDataArray(String entity, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        String[] ss = entity.split("\\.");
        JSONObject json = data;
        for (int i = 0; i < ss.length - 1; i++) {
            json = json.getJSONObject(ss[i]);
        }
        JSONArray array = json.getJSONArray(ss[ss.length - 1]);
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getObject(i, cls));
        }
        return list;
    }

    /**
     * 获取ApiPageData中分页list对象
     *
     * @param json 分页ApiPageData对象
     */
    public <T> List<T> getApiPageDataArray(JSONObject json, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JSONArray array = json.getJSONArray("messageList");
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getObject(i, cls));
        }
        return list;
    }

    /**
     * 获取ApiPageData中分页list对象
     */
    public <T> List<T> getApiPageDataArray(JSONObject json, String entity, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        JSONArray array = json.getJSONArray(entity);
        for (int i = 0; i < array.size(); i++) {
            list.add(array.getObject(i, cls));
        }
        return list;
    }

}

