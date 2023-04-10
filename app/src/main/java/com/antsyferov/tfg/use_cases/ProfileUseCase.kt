package com.antsyferov.tfg.use_cases

import android.net.Uri
import com.antsyferov.tfg.util.ResultOf


interface ProfileUseCase {

    suspend fun saveProfileChanges(name: String?, email: String?, uri: Uri?): ResultOf<Unit>

}