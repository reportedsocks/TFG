package com.tfg.domain.interfaces

import com.tfg.domain.models.data.Review
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface ReviewsDataSource {

    fun getReviews(articleId: String): Flow<ResultOf<List<Review>>>

    suspend fun addReview(review: Review): ResultOf<Unit>

}