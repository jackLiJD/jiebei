package com.ald.ebei.auth.model;

import com.ald.ebei.model.EbeiBaseModel;

/**
 * Created by sean yu on 2017/7/24.
 */

public class EbeiFaceLivenessModel extends EbeiBaseModel {
    private String imageBestUrl;
    private Double confidence;
    private String thresholds;

    public String getImageBestUrl() {
        return imageBestUrl;
    }

    public void setImageBestUrl(String imageBestUrl) {
        this.imageBestUrl = imageBestUrl;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getThresholds() {
        return thresholds;
    }

    public void setThresholds(String thresholds) {
        this.thresholds = thresholds;
    }
}
