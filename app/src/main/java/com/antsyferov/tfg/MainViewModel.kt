package com.antsyferov.tfg

import androidx.lifecycle.ViewModel
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.use_cases.PublicationsListUseCase
import com.antsyferov.tfg.use_cases.PublicationsListUseCaseImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.collections.List
@HiltViewModel
class MainViewModel @Inject constructor(
    val publicationsListUseCase: PublicationsListUseCase
): ViewModel() {
    fun getPublications(): Flow<List<Publication>>{
        return publicationsListUseCase.getPublications()
    }

}