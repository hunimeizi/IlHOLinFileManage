package com.lholin.filemanage

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.viewbinding.binding
import com.lholin.filemanage.databinding.ActivityMainBinding
import com.lholin.filemanage.filetools.FileRequest
import com.lholin.filemanage.filetools.iml.FileAccessFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by binding()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        //创建文件
        binding.btnCreateSAF.setOnClickListener {
            PermissionRequest(listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val fileRequest = FileRequest(File("FileManage/"))
                fileRequest.displayName = "message.txt"
                val fileResponse =
                    FileAccessFactory.getIFile(fileRequest).newCreateFile(this, fileRequest)
                if(fileResponse.isSuccess) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        BufferedOutputStream(contentResolver.openOutputStream(fileResponse.uri)).use {
                            it.write("你好啊，文件管理".toByteArray())
                        }
                    } else {
                        fileResponse.file?.let {
                            val fw = FileWriter(it.absolutePath)
                            fw.write("你好啊，文件管理")
                            fw.flush()
                            fw.close()
                        }
                    }
                    Toast.makeText(this, "文件创建成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "文件创建失败", Toast.LENGTH_SHORT).show()
                }

                //            val uri = MediaStore.Files.getContentUri("external")
//            val values = ContentValues()
//            val path = Environment.DIRECTORY_DOWNLOADS + "/FileManage"
//            values.put(MediaStore.Downloads.RELATIVE_PATH,path)
//            values.put(MediaStore.Downloads.DISPLAY_NAME,"Message.txt")
//            values.put(MediaStore.Downloads.TITLE,path)
//            val requestUri = contentResolver.insert(uri,values)
//            requestUri?.let {
//            val outputStream = contentResolver.openOutputStream(requestUri)
//                BufferedOutputStream(outputStream).use {
//                    it.write("你好啊，文件管理".toByteArray())
//                }
//            }
            }
        }

        //创建图片
        binding.btnInsertImage.setOnClickListener {
            val fileRequest = FileRequest(File("FileManage/"))
            fileRequest.displayName = "test.jpg"
            val fileResponse =
                FileAccessFactory.getIFile(fileRequest).newCreateFile(this, fileRequest)
            if(fileResponse.isSuccess) {
                val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.test)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentResolver.openOutputStream(fileResponse.uri).use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                } else {
                    fileResponse.file.outputStream().use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                        it.flush()
                    }
                }
                Toast.makeText(this, "添加图片成功", Toast.LENGTH_SHORT).show()
            }

//            val displayName = "test.jpg"
//            val values = ContentValues()
//            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, displayName)
//            values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg")
//            val imageUri =
//                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//            imageUri?.let {
//                contentResolver.openOutputStream(imageUri)?.use {
//                    BitmapFactory.decodeResource(resources, R.mipmap.test)
//                        .compress(Bitmap.CompressFormat.JPEG, 100, it)
//                }
//            }
        }

        // 查询删除图片
        binding.btnQueryImage.setOnClickListener {
            val fileRequest = FileRequest(File("FileManage/")).apply {
                displayName = "test.jpg"
            }
            val fileResponse = FileAccessFactory.getIFile(fileRequest).delete(this, fileRequest)
            Toast.makeText(this,
                if(fileResponse.isSuccess) "删除图片成功" else "删除图片失败",
                Toast.LENGTH_SHORT).show()
//            val extenrl = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            val selection = MediaStore.Images.Media.DISPLAY_NAME + "=?"
//            val cursor = contentResolver.query(extenrl,
//                arrayOf(MediaStore.Images.Media._ID),
//                selection,
//                arrayOf("test.jpg"),
//                null)
//            val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            if(cursor != null && cursor.moveToFirst()) {
//                val queryUri = ContentUris.withAppendedId(external, cursor.getLong(0))
//                contentResolver.delete(queryUri, null, null)
////                Toast.makeText(this, "查询成功 $queryUri", Toast.LENGTH_SHORT).show()
//                cursor.close()
//            }
        }

        // 修改文件名字
        binding.btnChangeImageName.setOnClickListener {
            val fileRequest = FileRequest(File("FileManage/"))
            fileRequest.displayName = "message.txt"
            val fileResponse =
                FileAccessFactory.getIFile(fileRequest)
                    .renameTo(this, fileRequest, FileRequest(File("FileManage/")).apply {
                        displayName = "newMessage.txt"
                    })
            if(fileResponse.isSuccess) {
                Toast.makeText(this, "修改文件名字成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "修改文件名字失败", Toast.LENGTH_SHORT).show()
            }
//            val extenrl = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            val selection = MediaStore.Images.Media.DISPLAY_NAME + "=?"
//            val cursor = contentResolver.query(extenrl,
//                arrayOf(MediaStore.Images.Media._ID),
//                selection,
//                arrayOf("test.jpg"),
//                null)
//            val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            if(cursor != null && cursor.moveToFirst()) {
//                val queryUri = ContentUris.withAppendedId(external, cursor.getLong(0))
//                val contentValues = ContentValues()
//                contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, "修改后的名字.jpg")
//                contentResolver.update(queryUri, contentValues, null, null)
//                cursor.close()
//            }
        }
    }
}