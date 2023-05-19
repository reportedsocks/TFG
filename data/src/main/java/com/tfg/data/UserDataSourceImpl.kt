package com.tfg.data

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import com.tfg.domain.interfaces.UserDataSource
import com.tfg.domain.models.data.Author
import com.tfg.domain.models.data.User
import com.tfg.domain.util.ResultOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tfg.domain.models.data.Customer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver
): UserDataSource {

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val auth = Firebase.auth

    private val TAG = "FIREBASE_DB"
    private val TAG_S = "FIREBASE_STORAGE"

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

    override suspend fun setUserRole(
        userId: String,
        role: Int,
        selectedPublication: String?,
        selectedArticle1: String?,
        selectedArticle2: String?,
        selectedArticle3: String?
    ): ResultOf<Unit> = suspendCoroutine { cont ->

        val changesMap = mutableMapOf<String, Any>()
        changesMap["role"] = role

        selectedPublication?.let {
            changesMap.put("publicationId", it)
        }
        selectedArticle1?.let {
            changesMap.put("articleId1", it)
        }
        selectedArticle2?.let {
            changesMap.put("articleId2", it)
        }
        selectedArticle3?.let {
            changesMap.put("articleId3", it)
        }


        db.collection("users").document(userId).set(
            changesMap,
            SetOptions.merge()
        ).addOnSuccessListener {
            cont.resume(ResultOf.Success(Unit))
        }.addOnFailureListener { e ->
            cont.resume(ResultOf.Failure(e))
        }

    }

    override fun getAuthor(userId: String): Flow<ResultOf<Author>> = callbackFlow {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { snapshot ->
                snapshot.toObject<Author>()?.let {
                    trySend(ResultOf.Success(it)).onFailure { Log.d(TAG, it.toString()) }
                }
            }
            .addOnFailureListener { e ->
                trySend(ResultOf.Failure(e)).onFailure { Log.d(TAG, it.toString()) }
            }

        awaitClose()
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
                if (task.isSuccessful) {

                    val changesMap = mutableMapOf<String, String>()
                    name?.let {
                        changesMap.put("name", name)
                    }
                    avatar?.let {
                        changesMap.put("photoUrl", it.toString())
                    }

                    db.collection("users").document(auth.uid ?: "").set(
                        changesMap,
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        cont.resume(ResultOf.Success(Unit))
                    }.addOnFailureListener {
                        cont.resume(ResultOf.Failure(it))
                    }

                } else
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
                if (task.isSuccessful) {
                    db.collection("users").document(auth.uid ?: "").set(
                        mapOf("email" to email),
                        SetOptions.merge()
                    ).addOnSuccessListener {
                        cont.resume(ResultOf.Success(Unit))
                    }.addOnFailureListener {
                        cont.resume(ResultOf.Failure(it))
                    }
                } else
                    cont.resume(ResultOf.Failure(task.exception))
            }
        } else {
            cont.resume(ResultOf.Failure(null))
        }
    }

    override suspend fun addUser(
        userId: String,
        name: String,
        email: String,
        phone: String,
        photoUrl: String
    ): ResultOf<Unit> = suspendCoroutine { cont ->
        db.collection("users").document(userId).set(
            mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "photoUrl" to photoUrl
            ),
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

    override fun getCustomers(): Flow<ResultOf<List<Customer>>> = callbackFlow {
        val registration = db.collection("users")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(ResultOf.Failure(error))
                }

                if (snapshot != null) {
                    val customers = mutableListOf<Customer>()
                    for (document in snapshot) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        customers.add(
                            document.toObject<Customer>().apply { id = document.id }
                        )

                    }
                    trySend(ResultOf.Success(customers))
                }

            }

        awaitClose { registration.remove() }

    }

}