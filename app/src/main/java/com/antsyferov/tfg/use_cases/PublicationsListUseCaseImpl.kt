package com.antsyferov.tfg.use_cases

import com.antsyferov.tfg.data.DataSource
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.util.ResultOf
import com.antsyferov.tfg.util.transform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class PublicationsListUseCaseImpl @Inject constructor(
    private val dataSource: DataSource
): PublicationsListUseCase {

    override fun getPublications(): Flow<ResultOf<List<Publication>>> {
        return dataSource.getPublications().map { result ->
           result.transform {
               map { firebasePublication ->
                   Publication(
                       id = firebasePublication.id,
                       title = firebasePublication.title,
                       description = firebasePublication.description,
                       status = getPublicationStatus(firebasePublication.review_date, firebasePublication.completion_date)
                   )
               }
           }
        }
    }

    private fun getPublicationStatus(reviewTimestamp: Long, completionTimestamp: Long): Publication.Status {
        val now = Date()
        val review = Date(reviewTimestamp)
        val completion = Date(completionTimestamp)

        return if (review.before(now))
            Publication.Status.OPEN
        else if (completion.before(now))
            Publication.Status.IN_REVIEW
        else
            Publication.Status.CLOSED
    }

}