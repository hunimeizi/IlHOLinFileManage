package com.lholin.filemanage.filetools.inter;

import android.content.Context;

import com.lholin.filemanage.filetools.BaseRequest;
import com.lholin.filemanage.filetools.FileResponse;

public interface IFile {

    <T extends BaseRequest> FileResponse newCreateFile(Context context, T baseRequest);

    <T extends BaseRequest> FileResponse delete(Context context, T baseRequest);

    <T extends BaseRequest> FileResponse renameTo(Context context, T where, T baseRequest);

//    <T extends BaseRequest> FileResponse copyFile(Context context, T baseRequest);

    <T extends BaseRequest> FileResponse query(Context context, T baseRequest);
}
