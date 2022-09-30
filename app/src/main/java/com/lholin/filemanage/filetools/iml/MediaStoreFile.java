package com.lholin.filemanage.filetools.iml;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lholin.filemanage.filetools.BaseRequest;
import com.lholin.filemanage.filetools.FileResponse;
import com.lholin.filemanage.filetools.annotation.DBFiled;
import com.lholin.filemanage.filetools.inter.IFile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressLint("NewApi")
public class MediaStoreFile implements IFile {

    private static volatile MediaStoreFile sInstance;
    public static final String AUDIO = "Audio";
    public static final String VIDEO = "Video";
    public static final String IMAGE = "Pictures";
    public static final String DOWNLOADS = "Downloads";//什么都可以放
    //外置卡的uri放到map
    HashMap<String, Uri> uriMap = new HashMap<>();

    private MediaStoreFile() {
        uriMap.put(AUDIO, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        uriMap.put(VIDEO, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        uriMap.put(IMAGE, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        uriMap.put(DOWNLOADS, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
    }

    public static MediaStoreFile getInstance() {
        if (sInstance == null) {
            synchronized (MediaStoreFile.class) {
                if (sInstance == null) {
                    sInstance = new MediaStoreFile();
                }
            }
        }
        return sInstance;
    }

    @Override
    public <T extends BaseRequest> FileResponse newCreateFile(Context context, T baseRequest) {
        Uri uri = uriMap.get(baseRequest.getType());
        ContentValues contentValues = objectConvertValues(baseRequest);
        Uri resultUri = context.getContentResolver().insert(uri, contentValues);
        FileResponse fileResponse = new FileResponse();
        if (resultUri != null) {
            fileResponse.setSuccess(true);
            fileResponse.setUri(resultUri);
        } else {
            fileResponse.setSuccess(false);
        }
        return fileResponse;
    }

    private <T extends BaseRequest> ContentValues objectConvertValues(T baseRequest) {
        ContentValues contentValues = new ContentValues();
        Field[] fields = baseRequest.getClass().getDeclaredFields();
        for (Field field : fields) {
            DBFiled dbField = field.getAnnotation(DBFiled.class);
            if (dbField == null) {
                continue;
            }
            String key = dbField.value();
            String value = null;

            String fieldName = field.getName();//path
            char firstLetter = Character.toUpperCase(fieldName.charAt(0));
            String theRest = fieldName.substring(1);
            String methodName = "get" + firstLetter + theRest;

            try {
                Method getMethod = baseRequest.getClass().getMethod(methodName);
                value = (String) getMethod.invoke(baseRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                contentValues.put(key, value);
            }
        }
        return contentValues;
    }

    @Override
    public <T extends BaseRequest> FileResponse delete(Context context, T baseRequest) {
        Uri uri = query(context, baseRequest).getUri();
        context.getContentResolver().delete(uri, null, null);
        return null;
    }

    @Override
    public <T extends BaseRequest> FileResponse renameTo(Context context, T where, T baseRequest) {
        Uri uri = query(context,where).getUri();
        FileResponse fileResponse = new FileResponse();
        if (uri == null){
            fileResponse.setSuccess(false);
            return fileResponse;
        }
        ContentValues contentValues = objectConvertValues(baseRequest);
        int code =context.getContentResolver().update(uri,contentValues,null,null);
        if (code > 0){
            fileResponse.setUri(uri);
            fileResponse.setSuccess(true);
        }else {
            fileResponse.setSuccess(false);
        }
        return fileResponse;
    }

    @Override
    public <T extends BaseRequest> FileResponse query(Context context, T baseRequest) {
        Uri uri = uriMap.get(baseRequest.getType());
        ContentValues contentValues = objectConvertValues(baseRequest);
        Condition condition = new Condition(contentValues);
        String[] projection = new String[]{MediaStore.Images.Media._ID};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri,projection,MediaStore.Images.Media.DISPLAY_NAME + "=?",new String[]{condition.whereArgs[0]},null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri queryUri = null;
        if (cursor!=null && cursor.moveToFirst()){
            queryUri = ContentUris.withAppendedId(uri,cursor.getLong(0));
            cursor.close();
        }
        FileResponse fileResponse = new FileResponse();
        if (queryUri!=null){
            fileResponse.setUri(queryUri);
            fileResponse.setSuccess(true);
        }else {
            fileResponse.setSuccess(false);
        }
        return fileResponse;
    }

    private class Condition {
        private String whereCasue;
        private String[] whereArgs;

        public Condition(ContentValues contentValues) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("1=1");
            ArrayList list = new ArrayList();
            Iterator<Map.Entry<String, Object>> set = contentValues.valueSet().iterator();
            while (set.hasNext()) {
                Map.Entry<String, Object> entry = set.next();
                String key = entry.getKey();
                String value = (String) entry.getValue();
                if (value != null) {
                    stringBuilder.append(" and " + key + " =? ");
                    list.add(value);
                }
            }
            this.whereCasue = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);
        }
    }
}
