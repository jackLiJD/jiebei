package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

import java.util.List;

/**
 * Created by johnvi on 2018/7/12.
 */

public class EbeiConversionLonLat extends EbeiBaseModel {


    /**
     * status : 1
     * info : OK
     * infocode : 10000
     * regeocode : {"formatted_address":"北京市海淀区燕园街道北京大学","addressComponent":{"country":"中国","province":"北京市","city":[],"citycode":"010","district":"海淀区","adcode":"110108","township":"燕园街道","towncode":"110108015000","neighborhood":{"name":"北京大学","type":"科教文化服务;学校;高等院校"},"building":{"name":"北京大学","type":"科教文化服务;学校;高等院校"},"streetNumber":{"street":"颐和园路","number":"5号","location":"116.310454,39.9927339","direction":"东北","distance":"94.5489"},"businessAreas":[{"location":"116.29522008325625,39.99426090286774","name":"颐和园","id":"110108"},{"location":"116.31060892521111,39.99231773703259","name":"北京大学","id":"110108"},{"location":"116.32013920092481,39.97507461118122","name":"中关村","id":"110108"}]},"pois":[{"id":"B000A816R6","name":"北京大学","type":"科教文化服务;学校;高等院校","tel":"010-62752114","direction":"东北","distance":"120.748","location":"116.31088,39.99281","address":"颐和园路5号","poiweight":"0.806322","businessarea":"颐和园"},{"id":"B000A85J18","name":"calis全国文理文献信息中心","type":"科教文化服务;学校;高等院校","tel":[],"direction":"东","distance":"44.4465","location":"116.310518,39.991893","address":"颐和园路5号北京大学","poiweight":"0.17778","businessarea":"颐和园"},{"id":"B000A85D1Z","name":"中国高校人文社会科学文献中心全国中心","type":"科教文化服务;科教文化场所;科教文化场所","tel":[],"direction":"西南","distance":"9.42475","location":"116.309910,39.991911","address":"颐和园路5号北京大学附近","poiweight":"0.196734","businessarea":"颐和园"},{"id":"B0FFFYT6CP","name":"北京大学陈翰生研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西北","distance":"33.3487","location":"116.309726,39.992169","address":"颐和园路5号北京大学","poiweight":"0.219387","businessarea":"颐和园"},{"id":"B000A192CC","name":"北京大学图书馆","type":"科教文化服务;图书馆;图书馆","tel":"010-62757167;010-62751051","direction":"西南","distance":"6.66562","location":"116.309972,39.991902","address":"颐和园路5号北京大学","poiweight":"0.556491","businessarea":"颐和园"},{"id":"B000A1FA1F","name":"北京大学中国古文献研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东南","distance":"124.57","location":"116.310584,39.990929","address":"颐和园路5号","poiweight":"0.266845","businessarea":"颐和园"},{"id":"B0FFG3BDN3","name":"北京大学二十世纪中国文化研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西","distance":"106.618","location":"116.308769,39.991798","address":"颐和园路5号北京大学","poiweight":"0.220494","businessarea":"颐和园"},{"id":"B0FFG3BFC1","name":"北京大学巴西文化中心","type":"科教文化服务;科研机构;科研机构|科教文化服务;文化宫;文化宫","tel":[],"direction":"西","distance":"120.261","location":"116.308651,39.992268","address":"颐和园路5号北京大学","poiweight":"0.220309","businessarea":"颐和园"},{"id":"B000A9QARU","name":"环境模拟与污染控制国家重点联合实验室(北京大学)","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.2881","businessarea":"颐和园"},{"id":"B000A7ORS4","name":"北京大学平民学校","type":"科教文化服务;学校;学校","tel":[],"direction":"东","distance":"169.273","location":"116.311762,39.992665","address":"颐和园路5号北京大学生物楼","poiweight":"0.273988","businessarea":"颐和园"},{"id":"B000A455B0","name":"北京大学中国持续发展研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.26699","businessarea":"颐和园"},{"id":"B000A80CVB","name":"北京大学环境与经济研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.395964","businessarea":"颐和园"},{"id":"B000A80CVA","name":"北京大学环境与健康研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.131912","businessarea":"颐和园"},{"id":"B000A7ORST","name":"北京大学外国哲学研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.298224","businessarea":"颐和园"},{"id":"B000A80CVC","name":"北京大学汉语语言学研究中心(五四路)","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.376","location":"116.311895,39.991291","address":"颐和园路5号北京大学","poiweight":"0.225997","businessarea":"颐和园"},{"id":"B000A9LEA8","name":"北京大学陈守仁国际研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"北","distance":"186.651","location":"116.310604,39.993571","address":"颐和园路5号北京大学","poiweight":"0.216722","businessarea":"颐和园"},{"id":"B000AA11FX","name":"北京大学妇女性别研究与培训基地","type":"科教文化服务;培训机构;培训机构","tel":[],"direction":"东","distance":"178.662","location":"116.311885,39.991248","address":"颐和园路5号北京大学五四路附近","poiweight":"0.188565","businessarea":"颐和园"},{"id":"B000AA4LVB","name":"北京大学中国环境经济学学科发展项目","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"161.834","location":"116.311873,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.21922","businessarea":"颐和园"},{"id":"B000A209FB","name":"北京大学政治发展与政府管理研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.288953","businessarea":"颐和园"},{"id":"B000A95CZ4","name":"北京大学妇女研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学五四路附近","poiweight":"0.242941","businessarea":"颐和园"},{"id":"B000A7OJH3","name":"北大欧洲中国研究合作中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.126582","businessarea":"颐和园"},{"id":"B000A26DAD","name":"北京大学邓小平理论研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学","poiweight":"0.304437","businessarea":"颐和园"},{"id":"B000A1B4B7","name":"北京大学经济法研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学凯原楼北京大学","poiweight":"0.311729","businessarea":"颐和园"},{"id":"B000AA11GF","name":"北京大学视觉与图像研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西南","distance":"198.219","location":"116.309175,39.990291","address":"颐和园路5号北京大学燕南园52号","poiweight":"0.240818","businessarea":"颐和园"},{"id":"B000AA1YH4","name":"北京大学生物基础教学实验中学生物化学实验室1","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"184.922","location":"116.312067,39.992472","address":"颐和园路5号北京大学文史教学楼附近","poiweight":"0.189818","businessarea":"颐和园"},{"id":"B0FFG4DZA1","name":"北京大学-广播台","type":"科教文化服务;传媒机构;传媒机构","tel":[],"direction":"东北","distance":"191.012","location":"116.311865,39.992914","address":"临湖路与未名北路交叉口南50米","poiweight":"0.202815","businessarea":"颐和园"},{"id":"B000A9PIG4","name":"北京大学静园六院","type":"科教文化服务;学校;学校","tel":[],"direction":"西","distance":"100.953","location":"116.308847,39.992156","address":"颐和园路5号北京大学","poiweight":"0.300935","businessarea":"颐和园"},{"id":"B0FFFDNE72","name":"北京大学静园五院","type":"科教文化服务;学校;学校","tel":[],"direction":"西","distance":"97.8593","location":"116.308908,39.991691","address":"颐和园路5号北京大学","poiweight":"0.218941","businessarea":"颐和园"},{"id":"B000A80D7Q","name":"北京大学第1教学楼","type":"科教文化服务;学校;学校","tel":[],"direction":"东北","distance":"103.557","location":"116.310408,39.992835","address":"颐和园路5号北京大学","poiweight":"0.308949","businessarea":"颐和园"},{"id":"B0FFFGYAZZ","name":"北京大学-静园四院","type":"科教文化服务;学校;学校","tel":[],"direction":"西南","distance":"126.82","location":"116.308928,39.991168","address":"颐和园路5号北京大学","poiweight":"0.204704","businessarea":"颐和园"}],"roads":[{"id":"010J50F0010195642","name":"求知路","direction":"南","distance":"67.7634","location":"116.31,39.9926"},{"id":"010J50F0010195645","name":"五四路","direction":"西","distance":"97.0266","location":"116.311,39.9919"},{"id":"010J50F0010199620","name":"科学路","direction":"西","distance":"115.312","location":"116.311,39.9919"}],"roadinters":[{"direction":"西北","distance":"178.52","location":"116.3112489,39.99066611","first_id":"010J50F0010195645","first_name":"五四路","second_id":"010J50F0010199620","second_name":"科学路"}],"aois":[{"id":"B000A816R6","name":"北京大学","adcode":"110108","location":"116.31088,39.99281","area":"1882561.803925","distance":"0","type":"141201"}]}
     */

    private String status;
    private String info;
    private String infocode;
    private RegeocodeBean regeocode;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public RegeocodeBean getRegeocode() {
        return regeocode;
    }

    public void setRegeocode(RegeocodeBean regeocode) {
        this.regeocode = regeocode;
    }

    public static class RegeocodeBean extends EbeiBaseModel {
        /**
         * formatted_address : 北京市海淀区燕园街道北京大学
         * addressComponent : {"country":"中国","province":"北京市","city":[],"citycode":"010","district":"海淀区","adcode":"110108","township":"燕园街道","towncode":"110108015000","neighborhood":{"name":"北京大学","type":"科教文化服务;学校;高等院校"},"building":{"name":"北京大学","type":"科教文化服务;学校;高等院校"},"streetNumber":{"street":"颐和园路","number":"5号","location":"116.310454,39.9927339","direction":"东北","distance":"94.5489"},"businessAreas":[{"location":"116.29522008325625,39.99426090286774","name":"颐和园","id":"110108"},{"location":"116.31060892521111,39.99231773703259","name":"北京大学","id":"110108"},{"location":"116.32013920092481,39.97507461118122","name":"中关村","id":"110108"}]}
         * pois : [{"id":"B000A816R6","name":"北京大学","type":"科教文化服务;学校;高等院校","tel":"010-62752114","direction":"东北","distance":"120.748","location":"116.31088,39.99281","address":"颐和园路5号","poiweight":"0.806322","businessarea":"颐和园"},{"id":"B000A85J18","name":"calis全国文理文献信息中心","type":"科教文化服务;学校;高等院校","tel":[],"direction":"东","distance":"44.4465","location":"116.310518,39.991893","address":"颐和园路5号北京大学","poiweight":"0.17778","businessarea":"颐和园"},{"id":"B000A85D1Z","name":"中国高校人文社会科学文献中心全国中心","type":"科教文化服务;科教文化场所;科教文化场所","tel":[],"direction":"西南","distance":"9.42475","location":"116.309910,39.991911","address":"颐和园路5号北京大学附近","poiweight":"0.196734","businessarea":"颐和园"},{"id":"B0FFFYT6CP","name":"北京大学陈翰生研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西北","distance":"33.3487","location":"116.309726,39.992169","address":"颐和园路5号北京大学","poiweight":"0.219387","businessarea":"颐和园"},{"id":"B000A192CC","name":"北京大学图书馆","type":"科教文化服务;图书馆;图书馆","tel":"010-62757167;010-62751051","direction":"西南","distance":"6.66562","location":"116.309972,39.991902","address":"颐和园路5号北京大学","poiweight":"0.556491","businessarea":"颐和园"},{"id":"B000A1FA1F","name":"北京大学中国古文献研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东南","distance":"124.57","location":"116.310584,39.990929","address":"颐和园路5号","poiweight":"0.266845","businessarea":"颐和园"},{"id":"B0FFG3BDN3","name":"北京大学二十世纪中国文化研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西","distance":"106.618","location":"116.308769,39.991798","address":"颐和园路5号北京大学","poiweight":"0.220494","businessarea":"颐和园"},{"id":"B0FFG3BFC1","name":"北京大学巴西文化中心","type":"科教文化服务;科研机构;科研机构|科教文化服务;文化宫;文化宫","tel":[],"direction":"西","distance":"120.261","location":"116.308651,39.992268","address":"颐和园路5号北京大学","poiweight":"0.220309","businessarea":"颐和园"},{"id":"B000A9QARU","name":"环境模拟与污染控制国家重点联合实验室(北京大学)","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.2881","businessarea":"颐和园"},{"id":"B000A7ORS4","name":"北京大学平民学校","type":"科教文化服务;学校;学校","tel":[],"direction":"东","distance":"169.273","location":"116.311762,39.992665","address":"颐和园路5号北京大学生物楼","poiweight":"0.273988","businessarea":"颐和园"},{"id":"B000A455B0","name":"北京大学中国持续发展研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.26699","businessarea":"颐和园"},{"id":"B000A80CVB","name":"北京大学环境与经济研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.395964","businessarea":"颐和园"},{"id":"B000A80CVA","name":"北京大学环境与健康研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"162.16","location":"116.311877,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.131912","businessarea":"颐和园"},{"id":"B000A7ORST","name":"北京大学外国哲学研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.298224","businessarea":"颐和园"},{"id":"B000A80CVC","name":"北京大学汉语语言学研究中心(五四路)","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.376","location":"116.311895,39.991291","address":"颐和园路5号北京大学","poiweight":"0.225997","businessarea":"颐和园"},{"id":"B000A9LEA8","name":"北京大学陈守仁国际研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"北","distance":"186.651","location":"116.310604,39.993571","address":"颐和园路5号北京大学","poiweight":"0.216722","businessarea":"颐和园"},{"id":"B000AA11FX","name":"北京大学妇女性别研究与培训基地","type":"科教文化服务;培训机构;培训机构","tel":[],"direction":"东","distance":"178.662","location":"116.311885,39.991248","address":"颐和园路5号北京大学五四路附近","poiweight":"0.188565","businessarea":"颐和园"},{"id":"B000AA4LVB","name":"北京大学中国环境经济学学科发展项目","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"161.834","location":"116.311873,39.991701","address":"颐和园路5号北京大学地学楼","poiweight":"0.21922","businessarea":"颐和园"},{"id":"B000A209FB","name":"北京大学政治发展与政府管理研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.288953","businessarea":"颐和园"},{"id":"B000A95CZ4","name":"北京大学妇女研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学五四路附近","poiweight":"0.242941","businessarea":"颐和园"},{"id":"B000A7OJH3","name":"北大欧洲中国研究合作中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"177.427","location":"116.311895,39.991290","address":"颐和园路5号北京大学","poiweight":"0.126582","businessarea":"颐和园"},{"id":"B000A26DAD","name":"北京大学邓小平理论研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学","poiweight":"0.304437","businessarea":"颐和园"},{"id":"B000A1B4B7","name":"北京大学经济法研究所","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"178.812","location":"116.311885,39.991245","address":"颐和园路5号北京大学凯原楼北京大学","poiweight":"0.311729","businessarea":"颐和园"},{"id":"B000AA11GF","name":"北京大学视觉与图像研究中心","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"西南","distance":"198.219","location":"116.309175,39.990291","address":"颐和园路5号北京大学燕南园52号","poiweight":"0.240818","businessarea":"颐和园"},{"id":"B000AA1YH4","name":"北京大学生物基础教学实验中学生物化学实验室1","type":"科教文化服务;科研机构;科研机构","tel":[],"direction":"东","distance":"184.922","location":"116.312067,39.992472","address":"颐和园路5号北京大学文史教学楼附近","poiweight":"0.189818","businessarea":"颐和园"},{"id":"B0FFG4DZA1","name":"北京大学-广播台","type":"科教文化服务;传媒机构;传媒机构","tel":[],"direction":"东北","distance":"191.012","location":"116.311865,39.992914","address":"临湖路与未名北路交叉口南50米","poiweight":"0.202815","businessarea":"颐和园"},{"id":"B000A9PIG4","name":"北京大学静园六院","type":"科教文化服务;学校;学校","tel":[],"direction":"西","distance":"100.953","location":"116.308847,39.992156","address":"颐和园路5号北京大学","poiweight":"0.300935","businessarea":"颐和园"},{"id":"B0FFFDNE72","name":"北京大学静园五院","type":"科教文化服务;学校;学校","tel":[],"direction":"西","distance":"97.8593","location":"116.308908,39.991691","address":"颐和园路5号北京大学","poiweight":"0.218941","businessarea":"颐和园"},{"id":"B000A80D7Q","name":"北京大学第1教学楼","type":"科教文化服务;学校;学校","tel":[],"direction":"东北","distance":"103.557","location":"116.310408,39.992835","address":"颐和园路5号北京大学","poiweight":"0.308949","businessarea":"颐和园"},{"id":"B0FFFGYAZZ","name":"北京大学-静园四院","type":"科教文化服务;学校;学校","tel":[],"direction":"西南","distance":"126.82","location":"116.308928,39.991168","address":"颐和园路5号北京大学","poiweight":"0.204704","businessarea":"颐和园"}]
         * roads : [{"id":"010J50F0010195642","name":"求知路","direction":"南","distance":"67.7634","location":"116.31,39.9926"},{"id":"010J50F0010195645","name":"五四路","direction":"西","distance":"97.0266","location":"116.311,39.9919"},{"id":"010J50F0010199620","name":"科学路","direction":"西","distance":"115.312","location":"116.311,39.9919"}]
         * roadinters : [{"direction":"西北","distance":"178.52","location":"116.3112489,39.99066611","first_id":"010J50F0010195645","first_name":"五四路","second_id":"010J50F0010199620","second_name":"科学路"}]
         * aois : [{"id":"B000A816R6","name":"北京大学","adcode":"110108","location":"116.31088,39.99281","area":"1882561.803925","distance":"0","type":"141201"}]
         */

        private String formatted_address;
        private AddressComponentBean addressComponent;
        private List<PoisBean> pois;
        private List<RoadsBean> roads;
        private List<RoadintersBean> roadinters;
        private List<AoisBean> aois;

        public String getFormatted_address() {
            return formatted_address;
        }

        public void setFormatted_address(String formatted_address) {
            this.formatted_address = formatted_address;
        }

        public AddressComponentBean getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressComponentBean addressComponent) {
            this.addressComponent = addressComponent;
        }

        public List<PoisBean> getPois() {
            return pois;
        }

        public void setPois(List<PoisBean> pois) {
            this.pois = pois;
        }

        public List<RoadsBean> getRoads() {
            return roads;
        }

        public void setRoads(List<RoadsBean> roads) {
            this.roads = roads;
        }

        public List<RoadintersBean> getRoadinters() {
            return roadinters;
        }

        public void setRoadinters(List<RoadintersBean> roadinters) {
            this.roadinters = roadinters;
        }

        public List<AoisBean> getAois() {
            return aois;
        }

        public void setAois(List<AoisBean> aois) {
            this.aois = aois;
        }

        public static class AddressComponentBean extends EbeiBaseModel {
            /**
             * country : 中国
             * province : 北京市
             * city : []
             * citycode : 010
             * district : 海淀区
             * adcode : 110108
             * township : 燕园街道
             * towncode : 110108015000
             * neighborhood : {"name":"北京大学","type":"科教文化服务;学校;高等院校"}
             * building : {"name":"北京大学","type":"科教文化服务;学校;高等院校"}
             * streetNumber : {"street":"颐和园路","number":"5号","location":"116.310454,39.9927339","direction":"东北","distance":"94.5489"}
             * businessAreas : [{"location":"116.29522008325625,39.99426090286774","name":"颐和园","id":"110108"},{"location":"116.31060892521111,39.99231773703259","name":"北京大学","id":"110108"},{"location":"116.32013920092481,39.97507461118122","name":"中关村","id":"110108"}]
             */

            private String country;
            private String province;
            private String citycode;
            private String district;
            private String adcode;
            private String township;
            private String towncode;
            private NeighborhoodBean neighborhood;
            private BuildingBean building;
            private StreetNumberBean streetNumber;
            private String city;
            private List<BusinessAreasBean> businessAreas;

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCitycode() {
                return citycode;
            }

            public void setCitycode(String citycode) {
                this.citycode = citycode;
            }

            public String getDistrict() {
                return district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }

            public String getTownship() {
                return township;
            }

            public void setTownship(String township) {
                this.township = township;
            }

            public String getTowncode() {
                return towncode;
            }

            public void setTowncode(String towncode) {
                this.towncode = towncode;
            }

            public NeighborhoodBean getNeighborhood() {
                return neighborhood;
            }

            public void setNeighborhood(NeighborhoodBean neighborhood) {
                this.neighborhood = neighborhood;
            }

            public BuildingBean getBuilding() {
                return building;
            }

            public void setBuilding(BuildingBean building) {
                this.building = building;
            }

            public StreetNumberBean getStreetNumber() {
                return streetNumber;
            }

            public void setStreetNumber(StreetNumberBean streetNumber) {
                this.streetNumber = streetNumber;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public List<BusinessAreasBean> getBusinessAreas() {
                return businessAreas;
            }

            public void setBusinessAreas(List<BusinessAreasBean> businessAreas) {
                this.businessAreas = businessAreas;
            }

            public static class NeighborhoodBean extends EbeiBaseModel {
                /**
                 * name : 北京大学
                 * type : 科教文化服务;学校;高等院校
                 */

                private String name;
                private String type;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }

            public static class BuildingBean extends EbeiBaseModel {
                /**
                 * name : 北京大学
                 * type : 科教文化服务;学校;高等院校
                 */

                private String name;
                private String type;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }

            public static class StreetNumberBean extends EbeiBaseModel {
                /**
                 * street : 颐和园路
                 * number : 5号
                 * location : 116.310454,39.9927339
                 * direction : 东北
                 * distance : 94.5489
                 */

                private String street;
                private String number;
                private String location;
                private String direction;
                private String distance;

                public String getStreet() {
                    return street;
                }

                public void setStreet(String street) {
                    this.street = street;
                }

                public String getNumber() {
                    return number;
                }

                public void setNumber(String number) {
                    this.number = number;
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getDirection() {
                    return direction;
                }

                public void setDirection(String direction) {
                    this.direction = direction;
                }

                public String getDistance() {
                    return distance;
                }

                public void setDistance(String distance) {
                    this.distance = distance;
                }
            }

            public static class BusinessAreasBean extends EbeiBaseModel {
                /**
                 * location : 116.29522008325625,39.99426090286774
                 * name : 颐和园
                 * id : 110108
                 */

                private String location;
                private String name;
                private String id;

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }

        public static class PoisBean extends EbeiBaseModel {
            /**
             * id : B000A816R6
             * name : 北京大学
             * type : 科教文化服务;学校;高等院校
             * tel : 010-62752114
             * direction : 东北
             * distance : 120.748
             * location : 116.31088,39.99281
             * address : 颐和园路5号
             * poiweight : 0.806322
             * businessarea : 颐和园
             */

            private String id;
            private String name;
            private String type;
            private String tel;
            private String direction;
            private String distance;
            private String location;
            private String address;
            private String poiweight;
            private String businessarea;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getTel() {
                return tel;
            }

            public void setTel(String tel) {
                this.tel = tel;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getPoiweight() {
                return poiweight;
            }

            public void setPoiweight(String poiweight) {
                this.poiweight = poiweight;
            }

            public String getBusinessarea() {
                return businessarea;
            }

            public void setBusinessarea(String businessarea) {
                this.businessarea = businessarea;
            }
        }

        public static class RoadsBean extends EbeiBaseModel {
            /**
             * id : 010J50F0010195642
             * name : 求知路
             * direction : 南
             * distance : 67.7634
             * location : 116.31,39.9926
             */

            private String id;
            private String name;
            private String direction;
            private String distance;
            private String location;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }
        }

        public static class RoadintersBean extends EbeiBaseModel {
            /**
             * direction : 西北
             * distance : 178.52
             * location : 116.3112489,39.99066611
             * first_id : 010J50F0010195645
             * first_name : 五四路
             * second_id : 010J50F0010199620
             * second_name : 科学路
             */

            private String direction;
            private String distance;
            private String location;
            private String first_id;
            private String first_name;
            private String second_id;
            private String second_name;

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getFirst_id() {
                return first_id;
            }

            public void setFirst_id(String first_id) {
                this.first_id = first_id;
            }

            public String getFirst_name() {
                return first_name;
            }

            public void setFirst_name(String first_name) {
                this.first_name = first_name;
            }

            public String getSecond_id() {
                return second_id;
            }

            public void setSecond_id(String second_id) {
                this.second_id = second_id;
            }

            public String getSecond_name() {
                return second_name;
            }

            public void setSecond_name(String second_name) {
                this.second_name = second_name;
            }
        }

        public static class AoisBean extends EbeiBaseModel {
            /**
             * id : B000A816R6
             * name : 北京大学
             * adcode : 110108
             * location : 116.31088,39.99281
             * area : 1882561.803925
             * distance : 0
             * type : 141201
             */

            private String id;
            private String name;
            private String adcode;
            private String location;
            private String area;
            private String distance;
            private String type;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getAdcode() {
                return adcode;
            }

            public void setAdcode(String adcode) {
                this.adcode = adcode;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getArea() {
                return area;
            }

            public void setArea(String area) {
                this.area = area;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
