package com.antsyferov.tfg.data

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.util.ResultOf
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseDataSource @Inject constructor(
    private val contentResolver: ContentResolver
): DataSource {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    private val TAG = "FIREBASE_DB"
    private val TAG_S = "FIREBASE_STORAGE"

    /*val publication = hashMapOf(
        "title" to "Test publication",
        "description" to "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
        "review_date" to "20-04-2023",
        "completion_date" to "20-05-2023"
    )

    fun addPublication() {
        db.collection("publications")
            .add(publication)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }*/

    override fun getPublications(): Flow<ResultOf<List<Publication>>> = callbackFlow {

        val successListener =
            OnSuccessListener<QuerySnapshot> { result ->
                val mutableList = mutableListOf<Publication>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    mutableList.add(
                        document.toObject<Publication>().apply { id = document.id }
                    )

                }
                trySend(ResultOf.Success(mutableList)).onFailure { Log.d(TAG, it.toString()) }
            }

        db.collection("publications")
            .get()
            .addOnSuccessListener(successListener)
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                trySend(ResultOf.Failure(exception)).onFailure { Log.d(TAG, it.toString()) }
            }

        awaitClose()
    }

    override fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> = callbackFlow {
        val successListener =
            OnSuccessListener<QuerySnapshot> { result ->
                val mutableList = mutableListOf<Article>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    mutableList.add(
                        document.toObject<Article>().apply { id = document.id }
                    )

                }
                trySend(ResultOf.Success(mutableList)).onFailure { Log.d(TAG, it.toString()) }
            }

        db.collection("publications/$publicationId/articles")
            .get()
            .addOnSuccessListener(successListener)
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
                trySend(ResultOf.Failure(exception)).onFailure { Log.d(TAG, it.toString()) }
            }

        awaitClose()
    }

    override suspend fun addArticle(publicationId: String, article: Article): ResultOf<String> = suspendCoroutine { cont ->
        db.collection("publications/$publicationId/articles")
            .add(article)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                cont.resume(ResultOf.Success(documentReference.id))
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                cont.resume(ResultOf.Failure(e))
            }
    }

    override suspend fun savePdf(
        articleId: String,
        uri: Uri
    ): ResultOf<Unit> = suspendCoroutine { cont ->
        val reference = storage.reference.child("articles/$articleId.pdf")
        contentResolver.openInputStream(uri)?.let { inputStream ->
            reference.putStream(inputStream).apply {
                addOnSuccessListener {
                    Log.d(TAG_S, it.toString())
                    inputStream.run { close() }
                    cont.resume(ResultOf.Success(Unit))
                }
                addOnFailureListener {
                    Log.d(TAG_S, it.toString())
                    inputStream.run { close() }
                    cont.resume(ResultOf.Failure(it))
                }
            }
        }
    }

}