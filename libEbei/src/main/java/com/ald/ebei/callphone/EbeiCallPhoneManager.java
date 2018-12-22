package com.ald.ebei.callphone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.ald.ebei.util.EbeiDataUtils;
import com.ald.ebei.util.EbeiMiscUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ald.ebei.config.EbeiConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/2/15 11:45
 * 描述：此类用于记录拨打电话的记录，以及把记录上传到相关的服务器
 * 修订历史：
 */
public class EbeiCallPhoneManager {
    public static final String LOG_TAG = "EbeiCallPhoneManager";
    private static final String PHONE_PREFS = "phonePrefs";
    private static final String FAILED_RECORDS = "failed_records_new_api_2.txt";
    public static final int REQUEST_READ_PHONE_STATE = 1100;


    private ReentrantLock isUploadingLock = new ReentrantLock();
    /**
     * 一些默认的电话，如果服务器取不到则使用这些号码
     */
    private static Map<String, String> defaultValue = new HashMap<String, String>();
    private static EbeiCallPhoneManager me = new EbeiCallPhoneManager();
    private final ReentrantReadWriteLock offlineStorageLock = new ReentrantReadWriteLock();
    private boolean hasNewPhoneCallLog;
    private EbeiPhoneCallRequest request;

    public static void initDefaultPhone(String event, String phone) {
        defaultValue.put(event, phone);
    }

    private static SharedPreferences getPreferences() {
        return EbeiConfig.getContext().getSharedPreferences(PHONE_PREFS, Context.MODE_PRIVATE);
    }

    public static EbeiCallPhoneManager getInstance() {
        return me;
    }

    public boolean hasNewPhoneCallLog() {
        return hasNewPhoneCallLog;
    }

    private static void saveAllCallRecords(List<EbeiPhoneCallLog> logs) {
        StringBuilder sb = new StringBuilder();
        for (EbeiPhoneCallLog log : logs) {
            sb.append(marshall(log)).append("\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        File file = EbeiDataUtils.createIfNotExistsOnPhone(FAILED_RECORDS);
        EbeiDataUtils.saveToFile(sb.toString(), file);
    }

    public void sendToServer() {
        if (isUploadingLock.isLocked()) {
            return;
        }
        EbeiConfig.execute(new Runnable() {
            @Override
            public void run() {
                if (isUploadingLock.tryLock()) {
                    try {
                        Thread.sleep(5000L);
                        doSendCallRecordList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        isUploadingLock.unlock();
                    }
                }
            }
        });
    }

    /**
     * 获取指定电话
     */
    public String getPhone(String key) {
        SharedPreferences share = getPreferences();
        return share.getString(key, defaultValue.get(key));
    }


    public void callPhone(EbeiPhoneCallRequest request) {
        if (request.isNeedConfirm()) {
            logPhoneCall(request);
            return;
        }
        try {
            makeCall(request.getPhone(), request.isTryCallFirst());
        } finally {
            logPhoneCall(request);
        }
    }

    private void makeCall(String phone, boolean tryCallFirst) {
        Intent intentForDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intentForDial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!tryCallFirst) {
            EbeiConfig.getContext().startActivity(intentForDial);
            return;
        }

        Intent intentForCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        intentForCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            if (ActivityCompat.checkSelfPermission( EbeiConfig.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            EbeiConfig.getContext().startActivity(intentForCall);
        } catch (Exception ex) {
            EbeiConfig.getContext().startActivity(intentForDial);
        }
    }

    private EbeiPhoneCallLog setConfirmLog(String phone, boolean isConfirmed) {
        List<EbeiPhoneCallLog> allLogs = readCallRecordList();
        if (allLogs == null) {
            return null;
        }
        int size = allLogs.size();
        EbeiPhoneCallLog lastLog = null;
        for (int i = size - 1; i >= 0; i--) {
            EbeiPhoneCallLog log = allLogs.get(i);
            if (log.getPhone().equals(phone)) {
                lastLog = log;
                break;
            }
        }
        if (lastLog == null || !lastLog.isNeedConfirm() || lastLog.getConfirmed() != EbeiPhoneCallLog.CONFIRMED_NOT_SET) {
            return null;
        }
        lastLog.setConfirmed(isConfirmed ? EbeiPhoneCallLog.CONFIRMED_YES : EbeiPhoneCallLog.CONFIRMED_NO);
        try {
            offlineStorageLock.writeLock().lock();
            saveAllCallRecords(allLogs);
        } finally {
            offlineStorageLock.writeLock().unlock();
        }
        return lastLog;
    }

    public void confirm(String phone) {
        EbeiPhoneCallLog log = setConfirmLog(phone, true);
        makeCall(phone, log != null && log.isTryCallFirst());
    }

    public void cancel(String phone) {
        setConfirmLog(phone, false);

        //cancel中无需统计通话时长，直接发送sendToServer; 而confirm会调用拨打电话，等待拨打结束统计了通话时长才调用sendToServer
        sendToServer();
    }

    private void logPhoneCall(final EbeiPhoneCallRequest request) {
        EbeiConfig.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    insertCallRecord(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void doSendCallRecordList() {
        final List<EbeiPhoneCallLog> logs = readCallRecordList();
        if (EbeiMiscUtils.isEmpty(logs)) {
            hasNewPhoneCallLog = false;
            return;
        }
        int maxBatchSize = 20;
        int size = logs.size();
        ArrayList<EbeiPhoneCallLog> batchLogs = new ArrayList<>();
        ArrayList<EbeiPhoneCallLog> failedLogs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            EbeiPhoneCallLog log = logs.get(i);
            if (log.getDuration() == EbeiPhoneCallLog.DURATION_CALL_LOG_NOT_EXAMINED) {
                long duration = -1;
                if ((log.isNeedConfirm() && log.getConfirmed() == EbeiPhoneCallLog.CONFIRMED_YES) || !log.isNeedConfirm()) {
                    long threshold = 10000L;// 最多允许10秒的误差
//                    duration = EbeiMiscUtils.getCallRecordDuration(log.getPhone(), log.getCallTime(), threshold);
                } else {
                    duration = EbeiPhoneCallLog.DURATION_CALL_LOG_NOT_FOUND;
                }
                log.setDuration((int) duration);
            }
            log.setNowTime(System.currentTimeMillis());
            batchLogs.add(log);
            if (batchLogs.size() < maxBatchSize && i < size - 1) {
                continue;
            }
//            EbeiApiResponse apiResponse = new PhoneCallLogApi().sendLog(batchLogs);
//            if (apiResponse == null || apiResponse.getCode() != 1000) {
//                failedLogs.addAll(batchLogs);
//            }
            batchLogs.clear();
        }
        try {
            offlineStorageLock.writeLock().lock();
            saveAllCallRecords(failedLogs);
        } finally {
            offlineStorageLock.writeLock().unlock();
            hasNewPhoneCallLog = false;
        }
    }

    private List<EbeiPhoneCallLog> readCallRecordList() {
        offlineStorageLock.readLock().lock();
        try {
            File file = EbeiDataUtils.createIfNotExistsOnPhone(FAILED_RECORDS);
            List<String> logStrings = EbeiDataUtils.readContentByLine(EbeiDataUtils.readFile(file));
            List<EbeiPhoneCallLog> logs = new ArrayList<>();
            for (String logString : logStrings) {
                if (TextUtils.isEmpty(logString)) {
                    continue;
                }
                try {
                    EbeiPhoneCallLog log = unmarshall(logString);
                    logs.add(log);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return logs;
        } finally {
            offlineStorageLock.readLock().unlock();
        }
    }

    private void insertCallRecord(EbeiPhoneCallRequest request) {
        List<EbeiPhoneCallLog> allLogs = readCallRecordList();
        EbeiPhoneCallLog callLog = new EbeiPhoneCallLog(request);
        for (EbeiPhoneCallLog log : allLogs) {
            if (callLog.getCallTime() == log.getCallTime() && callLog.getSource().equals(log.getSource()) && callLog
                    .getPhone().equals(log.getPhone())) {
                return;
            }
        }
        hasNewPhoneCallLog = true;
        allLogs.add(callLog);
        try {
            offlineStorageLock.writeLock().lock();
            saveAllCallRecords(allLogs);
        } finally {
            offlineStorageLock.writeLock().unlock();
        }
    }

    private static String marshall(EbeiPhoneCallLog log) {
        return JSON.toJSONString(log, SerializerFeature.WriteMapNullValue);
    }

    private static EbeiPhoneCallLog unmarshall(String logString) {
        return JSON.parseObject(logString, EbeiPhoneCallLog.class);
    }


}
