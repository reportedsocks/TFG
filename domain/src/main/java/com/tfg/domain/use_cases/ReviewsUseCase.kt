package com.tfg.domain.use_cases

import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.Review
import com.tfg.domain.util.ResultOf
import kotlinx.coroutines.flow.Flow

interface ReviewsUseCase {

    suspend fun addReview(articleId: String, articleAuthorId: String, authorId: String, review: Review): ResultOf<Unit>

    fun getReviews(articleId: String): Flow<ResultOf<List<Review>>>

}