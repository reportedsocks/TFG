package com.antsyferov.tfg.data

import android.util.Log
import com.antsyferov.tfg.data.models.Article
import com.antsyferov.tfg.data.models.Publication
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(): DataSource {

    private val db = Firebase.firestore

    private val TAG = "FIREBASE_DB"

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

    override fun getPublications(): Flow<List<Publication>> = callbackFlow {

        val successListener =
            OnSuccessListener<QuerySnapshot> { result ->
                val mutableList = mutableListOf<Publication>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    mutableList.add(
                        document.toObject<Publication>().apply { id = document.id }
                    )

                }
                trySend(mutableList).onFailure { Log.d(TAG, it.toString()) }
            }

        db.collection("publications")
            .get()
            .addOnSuccessListener(successListener)
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        awaitClose()
    }

    override fun getArticles(publicationId: String): Flow<List<Article>> = callbackFlow {
        val successListener =
            OnSuccessListener<QuerySnapshot> { result ->
                val mutableList = mutableListOf<Article>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    mutableList.add(
                        document.toObject<Article>().apply { id = document.id }
                    )

                }
                trySend(mutableList).onFailure { Log.d(TAG, it.toString()) }
            }

        db.collection("publications/$publicationId/articles")
            .get()
            .addOnSuccessListener(successListener)
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        awaitClose()
    }

    override fun addArticle(publicationId: String, article: Article) {
        db.collection("publications/$publicationId/articles")
            .add(article)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

}