package com.tfg.domain.use_cases

import com.tfg.domain.interfaces.PublicationsDataSource
import com.tfg.domain.models.ui.Publication
import com.tfg.domain.util.ResultOf
import com.tfg.domain.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class PublicationsListUseCaseImpl @Inject constructor(
    private val publicationsDataSource: PublicationsDataSource
): PublicationsListUseCase {

    override fun getPublications(): Flow<ResultOf<List<Publication>>> {
        return publicationsDataSource.getPublications().map { result ->
           result.transform {
               map { firebasePublication ->
                   Publication(
                       id = firebasePublication.id,
                       title = firebasePublication.title,
                       description = firebasePublication.description,
                       status = getPublicationStatus(firebasePublication.reviewDate, firebasePublication.completionDate),
                       reviewDate = firebasePublication.reviewDate,
                       finalSubmitDate = firebasePublication.finalSubmitDate,
                       completionDate = firebasePublication.completionDate
                   )
               }
           }
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

    private fun getPublicationStatus(review: Date, completion: Date): Publication.Status {
        val now = Date()

        return if (review.before(now))
            Publication.Status.OPEN
        else if (completion.before(now))
            Publication.Status.IN_REVIEW
        else
            Publication.Status.CLOSED
    }

}