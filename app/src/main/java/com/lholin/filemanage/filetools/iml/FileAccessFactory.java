package com.lholin.filemanage.filetools.iml;

import android.os.Build;
import com.lholin.filemanage.filetools.BaseRequest;
import com.lholin.filemanage.filetools.inter.IFile;

public class FileAccessFactory {

    public static IFile getIFile(BaseRequest baseRequest) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setFileType(baseRequest);
            return MediaStoreFile.getInstance();
        } else {
            return FileStoreFile.getInstance();
        }
    }

    private static void setFileType(BaseRequest request) {
        if (request.getFile().getAbsolutePath().endsWith(".mp3") || request.getFile().getAbsolutePath().endsWith(".wav")) {
            request.setType(MediaStoreFile.AUDIO);
        } else if (request.getFile().getAbsolutePath().startsWith(MediaStoreFile.VIDEO) ||
                request.getFile().getAbsolutePath().endsWith(".mp4") ||
                request.getFile().getAbsolutePath().endsWith(".rmvb") ||
                request.getFile().getAbsolutePath().endsWith(".avi")) {
            request.setType(MediaStoreFile.VIDEO);
        } else if (request.getFile().getAbsolutePath().startsWith(MediaStoreFile.IMAGE) ||
                request.getFile().getAbsolutePath().endsWith(".jpg") ||
                request.getFile().getAbsolutePath().endsWith(".png")) {
            request.setType(MediaStoreFile.IMAGE);
        } else
            request.setType(MediaStoreFile.DOWNLOADS);
    }
}
