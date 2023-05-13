package com.tfg.data

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.tfg.domain.interfaces.ReviewsDataSource
import com.tfg.domain.models.data.Review
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ReviewsDataSourceImpl @Inject constructor(): ReviewsDataSource {

    private val db = Firebase.firestore

    private val TAG = "FIREBASE_DB"

    override fun getReviews(articleId: String): Flow<ResultOf<List<Review>>> = callbackFlow {

        val registration = db.collection("reviews")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    trySend(ResultOf.Failure(error))
                }

                if (snapshot != null) {
                    val reviews = mutableListOf<Review>()
                    for (document in snapshot) {
                        Log.d(TAG, "${document.id} => ${document.data}")

                        reviews.add(
                            document.toObject<Review>().apply { id = document.id }
                        )

                    }
                    trySend(ResultOf.Success(reviews))
                }

            }

        awaitClose { registration.remove() }
    }

    override suspend fun addReview(review: Review): ResultOf<Unit> = suspendCoroutine { cont ->

        val reviewRef = db.collection("reviews").document()

        review.id = reviewRef.id

        reviewRef.set(review).addOnSuccessListener {
            cont.resume(ResultOf.Success(Unit))
        }.addOnFailureListener { e ->
            cont.resume(ResultOf.Failure(e))
        }

    }

}