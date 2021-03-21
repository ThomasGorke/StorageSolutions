package com.thomasgorke.storagesolution.core.local.firebase

import android.content.Context
import android.graphics.BitmapFactory
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.thomasgorke.storagesolution.core.model.FirebaseAuthor
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface FirebaseStorage {
    suspend fun getAllAuthors(): List<FirebaseAuthor>
    suspend fun insertAuthor(newAuthor: FirebaseAuthor): FirebaseAuthor
}


class FirebaseStorageImpl(
    private val context: Context,
    private val imageReference: StorageReference,
    private val firebaseStore: FirebaseFirestore
) : FirebaseStorage {

    override suspend fun getAllAuthors(): List<FirebaseAuthor> {
        val result = firebaseStore.collection("authors").get().await()

        Timber.d("Result: $result")
        return emptyList()
    }

    override suspend fun insertAuthor(newAuthor: FirebaseAuthor): FirebaseAuthor {
        firebaseStore.collection("authors").document().set(newAuthor).await()

        return newAuthor
    }

    private suspend fun uploadImage(image: ByteArray): String {
        return imageReference.putBytes(image).await().storage.downloadUrl.await().toString()
    }


    fun test() {



    }
}