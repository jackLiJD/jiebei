package com.ald.ebei.util;


import com.ald.ebei.util.log.EbeiLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Jacky Yu on 2016/1/23.
 */
public class EbeiSHAUtil {
    /***
     * SHA加密 生成SHA码
     * @param inStr
     * @return 返回SHA码
     */
    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            EbeiLogger.d("err shaEncode",inStr);
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] origBytes = sha.digest(byteArray);
        String tempStr = null;
        StringBuffer stb = new StringBuffer();
        for (int i = 0; i < origBytes.length; i++) {
            // 这里按位与是为了把字节转整时候取其正确的整数，java中一个int是4个字节
            // 如果origBytes[i]最高位为1，则转为int时，int的前三个字节都被1填充了
            tempStr = Integer.toHexString(origBytes[i] & 0xff);
            if (tempStr.length() == 1) {
                stb.append("0");
            }
            stb.append(tempStr);

        }
        return stb.toString();
    }


    /**
     * 计算sha256值
     *
     *@param signStrBefore 需要计算的字符串
     *@return
     */
    public static String getDigestStr(String signStrBefore) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(signStrBefore.getBytes());
            String tempStr = null;
            StringBuilder stb = new StringBuilder();
            byte[] origBytes = md.digest();
            for (int i = 0; i < origBytes.length; i++) {
                // 这里按位与是为了把字节转整时候取其正确的整数，java中一个int是4个字节
                // 如果origBytes[i]最高位为1，则转为int时，int的前三个字节都被1填充了
                tempStr = Integer.toHexString(origBytes[i] & 0xff);
                if (tempStr.length() == 1) {
                    stb.append("0");
                }
                stb.append(tempStr);

            }
            return stb.toString();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
            return "";
        }
    }
}
