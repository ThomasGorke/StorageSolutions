package com.thomasgorke.storagesolution.core.local.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.thomasgorke.storagesolution.core.model.Author
import com.thomasgorke.storagesolution.core.model.News
import com.thomasgorke.storagesolution.core.model.firebase.FirebaseAuthor
import com.thomasgorke.storagesolution.core.model.firebase.FirebaseNews
import com.thomasgorke.storagesolution.core.utils.toAuthor
import com.thomasgorke.storagesolution.core.utils.toByteArray
import com.thomasgorke.storagesolution.core.utils.toNews
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface FirebaseStorage {
    suspend fun getAllAuthors(): List<Author>
    suspend fun insertAuthor(newAuthor: Author): Author
    suspend fun deleteAuthor(authorId: String)

    suspend fun getAllNewsByAuthorId(authorId: String): List<News>
    suspend fun insertNews(news: News): News
    suspend fun updateNews(news: News): News
    suspend fun deleteNews(news: News)
}


class FirebaseStorageImpl(
    private val imageReference: StorageReference,
    private val firebaseStore: FirebaseFirestore
) : FirebaseStorage {

    private val authorsCollection = firebaseStore.collection("authors")

    override suspend fun getAllAuthors(): List<Author> {
        val result = authorsCollection.get().await()
        return result.map { it.toAuthor() }
    }

    override suspend fun insertAuthor(newAuthor: Author): Author {
        with(authorsCollection.document()) {
            val firebaseAuthor = FirebaseAuthor(
                newAuthor.name,
                uploadImage(id, newAuthor.image.toByteArray())
            )

            set(firebaseAuthor).await()
        }

        return newAuthor
    }

    private suspend fun uploadImage(reference: String, image: ByteArray): String {
        return imageReference
            .child(reference)
            .putBytes(image).await()
            .storage.downloadUrl.await()
            .toString()
    }

    override suspend fun deleteAuthor(authorId: String) {
        authorsCollection.document(authorId).delete()
    }

    override suspend fun getAllNewsByAuthorId(authorId: String): List<News> {
        val news = firebaseStore
            .collection("authors/${authorId}/news")
            .get().await()

        return news.map { it.toNews(authorId) }
    }

    override suspend fun insertNews(news: News): News {
        firebaseStore
            .collection("authors/${news.authorId}/news")
            .document()
            .set(FirebaseNews(news.headline, news.content)).await()

        return news
    }

    override suspend fun updateNews(news: News): News {
        Timber.d("News update: ${news}")
        authorsCollection
            .document(news.authorId)
            .collection("news")
            .document(news.id)
            .set(FirebaseNews(news.headline, news.content)).await()
        return news
    }

    override suspend fun deleteNews(news: News) {
        authorsCollection
            .document(news.authorId)
            .collection("news")
            .document(news.id)
            .delete()
    }
}