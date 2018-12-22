package com.ald.ebei.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;


import com.ald.ebei.model.EbeiContactsModel;

import java.util.ArrayList;

/**
 * 版权：XXX公司 版权所有
 * 作者：Jacky Yu
 * 版本：1.0
 * 创建日期：2017/3/1 15:51
 * 描述：
 * 修订历史：
 */
public class EbeiContactsUtils {

    /**
     * 获取库表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{Phone.DISPLAY_NAME, Phone.NUMBER, Phone.PHOTO_ID,
            Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    /**
     * 得到手机通讯录联系人信息
     **/
    private static ArrayList<EbeiContactsModel> getPhoneContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<EbeiContactsModel> ebeiContactsModels = new ArrayList<>();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (EbeiMiscUtils.isEmpty(phoneNumber) || !isNumeric(phoneNumber)) {
                    continue;
                }
                // 得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                EbeiContactsModel ebeiContactsModel = new EbeiContactsModel();
                ebeiContactsModel.setName(contactName);
                ebeiContactsModel.setPhoneNumber(phoneNumber);
                ebeiContactsModels.add(ebeiContactsModel);
            }
            phoneCursor.close();
        }
        return ebeiContactsModels;
    }

    /**
     * 得到手机SIM卡联系人人信息
     **/
    private static ArrayList<EbeiContactsModel> getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<EbeiContactsModel> models = new ArrayList<EbeiContactsModel>();
        // 获取Sims卡联系人
        Uri uri = Uri.parse("content://icc/adn");
//        Uri uri = Uri.parse("content://icc/adn");
//        Uri uri = Uri.parse("content://sim/adn");
        if (uri != null) {
//            Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
            Cursor phoneCursor;
            try {
                phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
            } catch (Exception e) {
                Uri uriTry = Uri.parse("content://com.android.contacts/raw_contacts");
                phoneCursor = resolver.query(uriTry, null, null, null, null);
            }

            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    // 得到手机号码
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (EbeiMiscUtils.isEmpty(phoneNumber) || !isNumeric(phoneNumber)) {
                        continue;
                    }
                    // 得到联系人名称
                    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

                    // Sim卡中没有联系人头像
                    EbeiContactsModel ebeiContactsModel = new EbeiContactsModel();
                    ebeiContactsModel.setName(contactName);
                    ebeiContactsModel.setPhoneNumber(phoneNumber);
                    models.add(ebeiContactsModel);
                }

                phoneCursor.close();
            }
        }
        return models;
    }

    /**
     * 获取通讯录(适配锤子手机。。。)
     */
    private static ArrayList<EbeiContactsModel> getContacts(Context context) {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        ArrayList<EbeiContactsModel> ebeiContactsModels = new ArrayList<>();
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        String[] arr = new String[cursor == null ? 0 : cursor.getCount()];
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                EbeiContactsModel ebeiContactsModel = new EbeiContactsModel();

                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        Phone.NUMBER
                };
                arr[i] = id + " , 姓名：" + name;

                ebeiContactsModel.setName(name);

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = context.getContentResolver().query(
                        Phone.CONTENT_URI,
                        phoneProjection,
                        Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    String numStr = "";
                    do {
                        String num = phonesCusor.getString(0);
                        arr[i] += " , 电话号码：" + num;
                        numStr += EbeiMiscUtils.isEmpty(numStr) ? num : "&" + num;
                    } while (phonesCusor.moveToNext());
                    ebeiContactsModel.setPhoneNumber(numStr);
                    phonesCusor.close();
                }
                ebeiContactsModels.add(ebeiContactsModel);
                i++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        return ebeiContactsModels;
    }

    private static boolean isNumeric(String s) {
        if (EbeiMiscUtils.isEmpty(s)) {
            return false;
        }
        String newString = s.trim().replace(" ", "").replace("-", "").replace("+", "");
        return EbeiMiscUtils.isNotEmpty(newString) && newString.matches("^[0-9]*$");
    }

    /**
     * 获取所有的联系人
     */
    public static ArrayList<EbeiContactsModel> getAllContacts(Context context) {
        ArrayList<EbeiContactsModel> ebeiContactsModels = getPhoneContacts(context);
        ArrayList<EbeiContactsModel> simModels = getSIMContacts(context);
        ebeiContactsModels.addAll(simModels);
        return ebeiContactsModels;

    }

    /**
     * 获取所有的联系人
     */
    public static String getPostContacts(Context context) {
        //如果本地存有通讯录就取本地通讯录
//        String contactsCache = EbeiMiscUtils.getSharepreferenceValue(APP_PREFERENCE_NAME, "contacts_info", "");
//        if (EbeiMiscUtils.isNotEmpty(contactsCache)) {
//            return contactsCache;
//        }
        ArrayList<EbeiContactsModel> ebeiContactsModels = new ArrayList<>();
        ArrayList<EbeiContactsModel> simModels = getSIMContacts(context);
        ArrayList<EbeiContactsModel> contacts = getContacts(context);
        ebeiContactsModels.addAll(simModels);
        ebeiContactsModels.addAll(contacts);
        StringBuilder stringBuilder = new StringBuilder();
        for (EbeiContactsModel model : ebeiContactsModels) {
            stringBuilder.append(model.toString());
        }
        if (EbeiMiscUtils.isEmpty(stringBuilder.toString())) {
            return "";
        }
        //存储通讯录
//        if (EbeiMiscUtils.isNotEmpty(stringBuilder.toString())) {
//            EbeiMiscUtils.setSharedPreferenceValue(APP_PREFERENCE_NAME, "contacts_info", stringBuilder.deleteCharAt(0).toString());
//        }
        return stringBuilder.deleteCharAt(0).toString();
    }


}
