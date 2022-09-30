package com.lholin.filemanage.filetools.iml;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.lholin.filemanage.filetools.BaseRequest;
import com.lholin.filemanage.filetools.FileRequest;
import com.lholin.filemanage.filetools.FileResponse;
import com.lholin.filemanage.filetools.inter.IFile;

import java.io.File;
import java.io.IOException;

public class FileStoreFile implements IFile {

    private static volatile FileStoreFile sInstance;

    public static FileStoreFile getInstance() {
        if (sInstance == null) {
            synchronized (FileStoreFile.class) {
                if (sInstance == null) {
                    sInstance = new FileStoreFile();
                }
            }
        }
        return sInstance;
    }

    @Override
    public <T extends BaseRequest> FileResponse newCreateFile(Context context, T baseRequest) {
        FileRequest fileRequest = (FileRequest) baseRequest;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileRequest.getPath());
        FileResponse fileResponse = new FileResponse();
        if (!file.exists()) {
            try {
                file.mkdirs();
                fileResponse.setSuccess(true);
            } catch (Exception e) {
                fileResponse.setSuccess(false);
                e.printStackTrace();
            }
        } else {
            if (!TextUtils.isEmpty(fileRequest.getDisplayName())) {
                File mFile = new File(file.getAbsolutePath(), fileRequest.getDisplayName());
                try {
                    mFile.createNewFile();
                    fileResponse.setSuccess(true);
                    fileResponse.setFile(new File(file.getAbsolutePath(), fileRequest.getDisplayName()));
                } catch (IOException e) {
                    fileResponse.setSuccess(false);
                    e.printStackTrace();
                }
            }
        }
        return fileResponse;
    }

    @Override
    public <T extends BaseRequest> FileResponse delete(Context context, T baseRequest) {
        FileRequest fileRequest = (FileRequest) baseRequest;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileRequest.getPath(),fileRequest.getDisplayName());
        FileResponse fileResponse = new FileResponse();
        if (file.exists()){
            file.delete();
            fileResponse.setSuccess(true);
        }else {
            fileResponse.setSuccess(false);
        }
        return fileResponse;
    }

    @Override
    public <T extends BaseRequest> FileResponse renameTo(Context context, T where, T baseRequest) {
        FileRequest whereRequest = (FileRequest) where;
        FileRequest fileRequest = (FileRequest) baseRequest;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + whereRequest.getPath(),whereRequest.getDisplayName());
        FileResponse fileResponse = new FileResponse();
        if (file.exists()){
            file.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + whereRequest.getPath(),fileRequest.getDisplayName()));
            fileResponse.setSuccess(true);
        }else {
            fileResponse.setSuccess(false);
        }
        return fileResponse;
    }

    @Override
    public <T extends BaseRequest> FileResponse query(Context context, T baseRequest) {
        return null;
    }
}
