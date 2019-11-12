package com.vike.query.wxpay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author: lsl
 * @createDate: 2019/11/13
 */
public class MyWXPayConfig extends WXPayConfig {

    private byte[] certData;

    private String appId;

    private String machId;

    private String key;

    public MyWXPayConfig() throws Exception {
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public MyWXPayConfig(String appId, String machId, String key) throws Exception {
        this.appId = appId;
        this.machId = machId;
        this.key = key;
        String certPath = "/path/to/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    @Override
    String getAppID() {
        return appId;
    }

    @Override
    String getMchID() {
        return machId;
    }

    @Override
    String getKey() {
        return key;
    }

    @Override
    InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    IWXPayDomain getWXPayDomain() {
        return null;
    }

    @Override
    public boolean shouldAutoReport(){return false;}

}
