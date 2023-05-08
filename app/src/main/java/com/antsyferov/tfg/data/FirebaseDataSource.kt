package com.antsyferov.tfg.data

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.data.models.User
import com.antsyferov.tfg.util.ResultOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseDataSource @Inject constructor(
    private val contentResolver: ContentResolver
): DataSource {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    private val TAG = "FIREBASE_DB"
    private val TAG_S = "FIREBASE_STORAGE"

    override fun getPublications(): Flow<ResultOf<List<Publication>>> = callbackFlow {

        val registration = db.collection("publications")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(ResultOf.Failure(error))
                }

                if (snapshot != null) {
                    val publications = mutableListOf<Publication>()
                    for (document in snapshot) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        publications.add(
                            document.toObject<Publication>().apply { id = document.id }
                        )

                    }
                    trySend(ResultOf.Success(publications))
                }

            }

        awaitClose { registration.remove() }
    }

    override fun getArticles(publicationId: String): Flow<ResultOf<List<Article>>> = callbackFlow {

        val registration = db.collection("publications/$publicationId/articles")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(ResultOf.Failure(error))
                }

                if (snapshot != null) {
                    val articles = mutableListOf<Article>()
                    for (document in snapshot) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        articles.add(
                            document.toObject<Article>().apply { id = document.id }
                        )

                    }
                    trySend(ResultOf.Success(articles))
                }

            }

        awaitClose { registration.remove() }
    }

    override fun getArticlesByUser(userId: String): Flow<ResultOf<List<Article>>> = callbackFlow {

        db.runTransaction { transaction ->
            val articles = mutableListOf<Article>()
            val userRef = db.collection("users").document(userId)
            val userSnapshot = transaction.get(userRef)

            val user = userSnapshot.toObject<User>()

            for (article in user?.articles ?: emptyList()) {

                val values = article.split("&sep")
                val publicationId = values[0]
                val articleId = values[1]
                val articleRef = db.collection("publications/$publicationId/articles").document(articleId)
                val articleSnapshot = transaction.get(articleRef)

                articleSnapshot.toObject<Article>()?.apply { id = articleId }?.let {
                    articles.add(it)
                }

            }

            articles

        }.addOnSuccessListener { articles ->
            trySend(ResultOf.Success(articles)).onFailure { Log.d(TAG, it.toString()) }
        }.addOnFailureListener { e ->
            trySend(ResultOf.Failure(e)).onFailure { Log.d(TAG, it.toString()) }
        }

        awaitClose()

    }

    override fun getArticleByAuthorId(articleId: String, authorId: String): Flow<ResultOf<Article>> = callbackFlow {
        db.runTransaction { transaction ->
            val articles = mutableListOf<Article>()
            val userRef = db.collection("users").document(authorId)
            val userSnapshot = transaction.get(userRef)

            val user = userSnapshot.toObject<User>()

            for (article in user?.articles ?: emptyList()) {

                val values = article.split("&sep")
                val publicationId = values[0]
                val articleIdFromUser = values[1]

                if (articleIdFromUser == articleId) {
                    val articleRef = db.collection("publications/$publicationId/articles").document(articleId)
                    val articleSnapshot = transaction.get(articleRef)

                    articleSnapshot.toObject<Article>()?.apply { id = articleId }?.let {
                        articles.add(it)
                    }
                }
            }

            articles

        }.addOnSuccessListener { articles ->
            trySend(ResultOf.Success(articles.first())).onFailure { Log.d(TAG, it.toString()) }
        }.addOnFailureListener { e ->
            trySend(ResultOf.Failure(e)).onFailure { Log.d(TAG, it.toString()) }
        }

        awaitClose()
    }

    override fun getArticleByPublicationId(
        articleId: String,
        publicationId: String
    ): Flow<ResultOf<Article>> = callbackFlow {
        db.collection("publications/$publicationId/articles").document(articleId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject<Article>()?.let {
                    trySend(ResultOf.Success(it)).onFailure { Log.d(TAG, it.toString()) }
                }
            }
            .addOnFailureListener { e ->
                trySend(ResultOf.Failure(e)).onFailure { Log.d(TAG, it.toString()) }
            }

        awaitClose()
    }

    override fun getUserRole(userId: String): Flow<ResultOf<Int>> = callbackFlow {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject<User>()?.role?.let {
                    trySend(ResultOf.Success(it)).onFailure { Log.d(TAG, it.toString()) }
                }
            }
            .addOnFailureListener { e ->
                trySend(ResultOf.Failure(e)).onFailure { Log.d(TAG, it.toString()) }
            }

        awaitClose()
    }

    override suspend fun addArticle(publicationId: String, article: Article): ResultOf<String> = suspendCoroutine { cont ->

        var articleId = ""

        db.runBatch { batch ->

            val articleRef = db.collection("publications/$publicationId/articles").document()

            batch.set(articleRef, article)

            articleId = articleRef.id
            val userId = auth.currentUser?.uid ?: ""
            val usersRef = db.collection("users").document(userId)
            val value = "$publicationId&sep$articleId"

            batch.update(usersRef, "articles", FieldValue.arrayUnion(value))

        }.addOnSuccessListener {
            cont.resume(ResultOf.Success(articleId))
        }.addOnFailureListener { e ->
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

    override suspend fun saveAvatar(uri: Uri): ResultOf<Uri> = suspendCoroutine { cont ->
        val userId = auth.currentUser?.uid ?: ""
        val reference = storage.reference.child("avatars/$userId")
        contentResolver.openInputStream(uri)?.let { inputStream ->
            reference
                .putStream(inputStream)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        cont.resume(ResultOf.Failure(task.exception))
                    }
                    reference.downloadUrl
                }
                .addOnCompleteListener { urlTask ->
                    cont.resume(
                        if (urlTask.isSuccessful)
                            ResultOf.Success(urlTask.result)
                        else
                            ResultOf.Failure(urlTask.exception)
                    )
                }
        }
    }

    override suspend fun updateUserNameAndAvatar(name: String?, avatar: Uri?): ResultOf<Unit> = suspendCoroutine { cont ->
        val user = auth.currentUser
        if (user != null) {
            user.updateProfile(
                userProfileChangeRequest {
                    name?.let { displayName = it }
                    avatar?.let { photoUri = it }
                }
            ).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    cont.resume(ResultOf.Success(Unit))
                else
                    cont.resume(ResultOf.Failure(task.exception))
            }
        } else {
            cont.resume(ResultOf.Failure(null))
        }

    }

    override suspend fun updateUserEmail(email: String): ResultOf<Unit> = suspendCoroutine { cont ->
        val user = auth.currentUser
        if (user != null) {
            user.updateEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    cont.resume(ResultOf.Success(Unit))
                else
                    cont.resume(ResultOf.Failure(task.exception))
            }
        } else {
            cont.resume(ResultOf.Failure(null))
        }
    }

    override suspend fun addUser(userId: String): ResultOf<Unit> = suspendCoroutine { cont ->
        db.collection("users").document(userId).set(
            emptyMap<String, String>(),
            SetOptions.merge()
        ).apply {
            addOnSuccessListener {
                cont.resume(ResultOf.Success(Unit))
            }
            addOnFailureListener {
                cont.resume(ResultOf.Failure(it))
            }
        }
    }

}