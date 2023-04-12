package com.antsyferov.tfg.data

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.antsyferov.tfg.data.models.User
import com.antsyferov.tfg.util.ResultOf
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
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

    override suspend fun addUser(userId: String): ResultOf<Unit> = suspendCoroutine{ cont ->
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