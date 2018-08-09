package com.religion76.firebasechatkit

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.FileInputStream
import java.util.*


/**
 * Created by SunChao on 2018/8/8.
 */
object StorageManager {

    fun uploadLocalImage(sessionId: String, path: String, onCompleted: ((imgUri: Uri?) -> Unit)? = null) {
        val imgRef = createRef(sessionId, path)
        upload(imgRef, path, onCompleted)
    }

    private fun createRef(sessionId: String, path: String): StorageReference {
        return FirebaseStorage.getInstance()
                .reference
                .child("chat_image")
                .child("$sessionId/${UUID.randomUUID()}.${path.substringAfterLast(".")}")
    }

    private fun upload(imgRef: StorageReference, filePath: String, onCompleted: ((imgUri: Uri?) -> Unit)? = null) {
        val task = imgRef.putStream(FileInputStream(filePath))

        task.addOnCompleteListener {
            if (it.isSuccessful) {
                imgRef.downloadUrl.addOnCompleteListener {
                    onCompleted?.invoke(it.result)
                }
            } else {
                onCompleted?.invoke(null)
            }
        }
    }
}