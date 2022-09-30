package com.lholin.filemanage.filetools;

import android.os.Environment;
import android.provider.MediaStore;

import com.lholin.filemanage.filetools.annotation.DBFiled;

import java.io.File;

public class FileRequest extends BaseRequest {

    @DBFiled(MediaStore.Downloads.DISPLAY_NAME)
    private String displayName;
    @DBFiled(MediaStore.Downloads.TITLE)
    private String title;
    @DBFiled(MediaStore.Downloads.RELATIVE_PATH)
    private String path;

    public FileRequest(File file) {
        super(file);
        this.path = file.getName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return Environment.DIRECTORY_DOWNLOADS + "/" + path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
