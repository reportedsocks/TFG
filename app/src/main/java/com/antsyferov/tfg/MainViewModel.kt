package com.antsyferov.tfg

import androidx.lifecycle.ViewModel
import com.antsyferov.tfg.ui.models.Publication
import com.antsyferov.tfg.use_cases.PublicationsListUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

class MainViewModel: ViewModel() {
    fun getPublications(): Flow<List<Publication>>{
        return PublicationsListUseCaseImpl().getPublications()
    }

}