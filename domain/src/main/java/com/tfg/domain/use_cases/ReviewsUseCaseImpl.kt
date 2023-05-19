package com.tfg.domain.use_cases

import com.tfg.domain.interfaces.PublicationsDataSource
import com.tfg.domain.interfaces.ReviewsDataSource
import com.tfg.domain.models.ui.Article
import com.tfg.domain.models.ui.Review
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReviewsUseCaseImpl @Inject constructor(
    private val reviewsDataSource: ReviewsDataSource,
    private val publicationsDataSource: PublicationsDataSource
): ReviewsUseCase {
    override suspend fun addReview(articleId: String, articleAuthorId: String, authorId: String, review: Review): ResultOf<Unit> {

        val resultPublicationId = publicationsDataSource.getPublicationIdByArticle(articleId, articleAuthorId)

        val publicationId = (resultPublicationId as? ResultOf.Success)?.data ?: ""

        return reviewsDataSource.addReview(com.tfg.domain.models.data.Review(
            articleId = articleId,
            publicationId = publicationId,
            articleAuthorId = articleAuthorId,
            reviewAuthorId = authorId,
            rating = review.rating,
            description = review.description,
            relevance = review.relevance,
            comment = review.comment
        ))

    }

    override fun getReviews(articleId: String): Flow<ResultOf<List<Review>>> {
        return reviewsDataSource.getReviews(articleId).map { result ->
            result.transform {
                map { review ->
                    Review(
                        id = review.id,
                        rating = review.rating,
                        description = review.description,
                        relevance = review.relevance,
                        comment = review.comment,
                        reviewAuthorId = review.reviewAuthorId,
                        createdAt = review.createdAt
                    )
                }
            }
        }
    }


}
