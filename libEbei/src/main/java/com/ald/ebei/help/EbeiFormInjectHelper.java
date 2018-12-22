package com.ald.ebei.help;


import android.webkit.JavascriptInterface;

import com.ald.ebei.config.EbeiConfig;
import com.ald.ebei.util.EbeiMiscUtils;

import java.util.List;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:29
 * 描述：
 * 修订历史：
 */
public class EbeiFormInjectHelper {
    private List<String> formInjectUrlList;
    private String loadURL;
    private String injectResId;

    public void init() {
        EbeiConfig.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                    formInjectUrlList = new FromInjectApi().getUrlList();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @JavascriptInterface
    public void inject(String actionUrl, String postData) {
        doInjectIfNeed(actionUrl, postData);
    }

    public void doInjectIfNeed(final String actionUrl, final String postData) {
        List<String> list = formInjectUrlList;
        if (EbeiMiscUtils.isEmpty(list)) {
            return;
        }
        String theUrl = null;
        for (String url : list) {
            if (actionUrl.startsWith(url)) {
                theUrl = url;
                break;
            }
        }
        if (EbeiMiscUtils.isEmpty(theUrl)) {
            return;
        }
        // 这个判断是为了避免post请求重复拦截。具体的场景如下：
        // webview有一个post请求到http://abc.com/hello.htm，数据体是{'name':'mucang'}。
        // 1. 在webview的onLoadResource会看到这个请求，但是并不知道是post，也看不到数据体，会调用inject("http://abc.com", "")；
        // 2. 为了弥补1中的不足，js部分会把上面的post和数据体两个信息通过调用注入到js中的对象的inject方法来告诉java部分，即调用
        // inject("http://abc.com", "{\"name\":\"mucang\"}")；
        // 因此对于1中的诸如请求应该是要丢掉的，一是没有意义，二是与2中拦截到的重复。
        //
        // 去重的逻辑是如下实现的：
        // 因为诸如url列表中的url都是hello.htm形式（即不带查询参数，不是hello.htm?a=b的形式），所以如果传入的actionUrl和列表中的某个url完
        // 全一样，那么actionUrl也是不带查询参数的形式，因此只能认为它是一个post请求的url。这也是为什么必须要求postData不为空才有意义。
        if (theUrl.equals(actionUrl) && EbeiMiscUtils.isEmpty(postData)) {
            return;
        }
        EbeiConfig.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FormInjectData data = new FormInjectData();
                    data.setInjectResId(injectResId);
                    data.setWebsite(loadURL);
                    data.setUrl(actionUrl);
                    data.setPost(postData);
//                    new FromInjectApi().postInjectData(data);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void setLoadURL(String loadURL) {
        this.loadURL = loadURL;
    }

    public void setInjectResId(String injectResId) {
        this.injectResId = injectResId;
    }


    private static class FormInjectData {
        private String website;
        private String url;
        private String post;
        private String injectResId;

        public String getInjectResId() {
            return injectResId;
        }

        public void setInjectResId(String injectResId) {
            this.injectResId = injectResId;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }
    }
}
