package com.lholin.filemanage.filetools

import android.content.ContentValues
import android.text.TextUtils
import com.lholin.filemanage.filetools.annotation.DBFiled
import java.lang.reflect.Field
import java.lang.reflect.Method

fun <T : BaseRequest?> objectConvertValues(baseRequest: T): ContentValues {
    val contentValues = ContentValues()
    val fields: Array<Field> = (baseRequest as FileRequest)::class.java.declaredFields
    for(field in fields) {
        val dbField = field.getAnnotation(DBFiled::class.java) ?: continue
        val key: String = dbField.value
        var value: String? = null
        val fieldName = field.name //path
        val firstLetter = fieldName[0].uppercaseChar()
        val theRest = fieldName.substring(1)
        val methodName = "get$firstLetter$theRest"
        try {
            val getMethod: Method = (baseRequest as FileRequest)::class.java.getMethod(methodName)
            value = getMethod.invoke(baseRequest) as String
        } catch(e: Exception) {
            e.printStackTrace()
        }
        if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            contentValues.put(key, value)
        }
    }
    return contentValues
}