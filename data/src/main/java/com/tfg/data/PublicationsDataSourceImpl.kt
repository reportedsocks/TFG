package com.tfg.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tfg.domain.interfaces.PublicationsDataSource
import com.tfg.domain.models.data.Article
import com.tfg.domain.models.data.Publication
import com.tfg.domain.models.data.User
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PublicationsDataSourceImpl @Inject constructor(): PublicationsDataSource {

    private val db = Firebase.firestore

    private val TAG = "FIREBASE_DB"

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

    override suspend fun addPublication(publication: Publication): ResultOf<Unit> = suspendCoroutine { cont ->

        val publicationRef = db.collection("publications").document()

        publication.id = publicationRef.id

        publicationRef.set(publication).addOnSuccessListener {
            cont.resume(ResultOf.Success(Unit))
        }.addOnFailureListener { e ->
            cont.resume(ResultOf.Failure(e))
        }

    }

    override suspend fun getPublicationIdByArticle(
        articleId: String,
        authorId: String
    ): ResultOf<String> = suspendCoroutine { cont ->
        db.collection("users").document(authorId)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject<User>()
                for (articleEntry in user?.articles ?: emptyList()) {

                    val values = articleEntry.split("&sep")
                    val publicationId = values[0]
                    val articleEntryId = values[1]

                    if (articleId == articleEntryId) {
                        cont.resume(ResultOf.Success(publicationId))
                    }
                }
            }
            .addOnFailureListener { e -> cont.resume(ResultOf.Failure(e)) }

    }

    override suspend fun getPublication(publicationId: String): ResultOf<Publication> = suspendCoroutine { cont ->
        db.collection("publications")
            .whereEqualTo("id", publicationId)
            .get()
            .addOnSuccessListener {
                val publication = it.documents.firstOrNull()?.toObject<Publication>()
                if (publication != null) {
                    cont.resume(ResultOf.Success(publication))
                } else {
                    cont.resume(ResultOf.Failure(null))
                }
            }
            .addOnFailureListener { e ->
                cont.resume(ResultOf.Failure(e))
            }
    }

}