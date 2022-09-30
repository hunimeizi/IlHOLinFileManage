package com.lholin.filemanage

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.lholin.filemanage.view.PermissionCustomDialog
import com.permissionx.guolindev.PermissionX

fun FragmentActivity.PermissionRequest(permissions: List<String>, permissionSuccess: () -> Unit) {
    PermissionX.init(this)
        .permissions(permissions)
        .onForwardToSettings { scope, deniedList -> //拒绝且不再询问
            val message = "您需要去设置中手动开启以下权限"
            val dialog = PermissionCustomDialog(this, message, deniedList)
            scope.showForwardToSettingsDialog(dialog)
        }.request { allGranted, _, _ ->
            if(allGranted) {
                permissionSuccess()
            } else {
                Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show()
                //记得再次显示升级框
            }
        }
}