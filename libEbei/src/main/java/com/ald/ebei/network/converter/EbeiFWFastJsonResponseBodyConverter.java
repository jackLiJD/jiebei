package com.ald.ebei.network.converter;


import com.ald.ebei.network.EbeiNetworkUtil;
import com.ald.ebei.network.entity.EbeiApiResponse;
import com.ald.ebei.network.exception.EbeiApiException;
import com.ald.ebei.util.log.EbeiLogger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.network.entity.ApiResponseEbei;
import com.ald.ebei.network.exception.EbeiApiExceptionEnum;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class EbeiFWFastJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    private Type mType;

    private ParserConfig config;
    private int featureValues;
    private Feature[] features;

    EbeiFWFastJsonResponseBodyConverter(Type type, ParserConfig config, int featureValues,
                                        Feature... features) {
        mType = type;
        this.config = config;
        this.featureValues = featureValues;
        this.features = features;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        /**
         * 网络加载动画close
         */
        EbeiNetworkUtil.dismissCutscenes();
        /***
         * 此处的value只能使用一次，否则会出现错误
         */
        String response = value.string().trim();
        if (EbeiConfig.isDebug()) {
            EbeiLogger.d("watermelon", "response >> \n" + response);
        }
        try {
            /*
             * ApiResponse结构体解析
             */
            JSONObject jsonObjectResp = JSON.parseObject(response);
            ApiResponseEbei apiResponse = new ApiResponseEbei(jsonObjectResp);
            /*
             * 返回非成功的直接抛异常
             */
            if (apiResponse.getCode() != EbeiApiExceptionEnum.SUCCESS.getErrorCode()) {
                throw new EbeiApiException(apiResponse.getCode(), apiResponse.getMsg());
            }
            T model = apiResponse.getData(mType);
            if (model == null) {
                String className = mType.toString();
                String cls = Boolean.class.getSimpleName();
                String clsApiResponse = EbeiApiResponse.class.getSimpleName();
                if (className.contains(cls)) {
                    return (T) Boolean.class.cast(true);
                }
                if (className.contains(clsApiResponse)) {
                    return (T) apiResponse;
                }
                throw new EbeiApiException(EbeiApiExceptionEnum.EMPTY.getErrorCode(), EbeiApiExceptionEnum.EMPTY.getErrorMsg());
            }
            return model;
        } finally {
            value.close();
        }
    }
}
