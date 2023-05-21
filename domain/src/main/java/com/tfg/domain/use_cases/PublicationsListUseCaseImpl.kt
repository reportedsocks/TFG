package com.tfg.domain.use_cases

import com.tfg.domain.interfaces.PublicationsDataSource
import com.tfg.domain.interfaces.UserDataSource
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class PublicationsListUseCaseImpl @Inject constructor(
    private val publicationsDataSource: PublicationsDataSource,
    private val userDataSource: UserDataSource
): PublicationsListUseCase {

    override fun getPublications(): Flow<ResultOf<List<Publication>>> {
        return publicationsDataSource.getPublications().map { result ->
           result.transform {
               map { firebasePublication ->
                   Publication(
                       id = firebasePublication.id,
                       title = firebasePublication.title,
                       description = firebasePublication.description,
                       status = getPublicationStatus(firebasePublication.reviewDate, firebasePublication.finalSubmitDate, firebasePublication.completionDate),
                       reviewDate = firebasePublication.reviewDate,
                       finalSubmitDate = firebasePublication.finalSubmitDate,
                       completionDate = firebasePublication.completionDate
                   )
               }
           }
        }
    }

    override suspend fun getPublication(articleId: String, authorId: String): ResultOf<Publication> {
        val resultId = publicationsDataSource.getPublicationIdByArticle(articleId, authorId)
        return if (resultId is ResultOf.Success) {
            val publicationId = resultId.data
            publicationsDataSource.getPublication(publicationId).transform {
                Publication(
                    id = id,
                    title = title,
                    description = description,
                    status = getPublicationStatus(reviewDate, finalSubmitDate, completionDate),
                    reviewDate = reviewDate,
                    finalSubmitDate = finalSubmitDate,
                    completionDate = completionDate
                )
            }
        } else ResultOf.Failure(null)

    }

    override suspend fun getPublicationById(publicationId: String): ResultOf<Publication> {
        return publicationsDataSource.getPublication(publicationId).transform {
            Publication(
                id = id,
                title = title,
                description = description,
                status = getPublicationStatus(reviewDate, finalSubmitDate, completionDate),
                reviewDate = reviewDate,
                finalSubmitDate = finalSubmitDate,
                completionDate = completionDate
            )
        }
    }

    override suspend fun addPublication(publication: Publication): ResultOf<Unit> {
        return publicationsDataSource.addPublication(
            com.tfg.domain.models.data.Publication(
                title = publication.title,
                description = publication.description,
                reviewDate = publication.reviewDate,
                finalSubmitDate = publication.finalSubmitDate,
                completionDate = publication.completionDate
            )
        )
    }

    private fun getPublicationStatus(review: Date, finalSubmit: Date, completion: Date): Publication.Status {
        val now = Date()

        return if (now.before(review))
            Publication.Status.OPEN
        else if (now.before(finalSubmit))
            Publication.Status.IN_REVIEW
        else if (now.before(completion))
            Publication.Status.FINAL_SUBMIT
        else
            Publication.Status.CLOSED
    }

}