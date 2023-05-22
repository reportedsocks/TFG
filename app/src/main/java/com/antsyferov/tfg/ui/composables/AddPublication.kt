package com.antsyferov.tfg.ui.composables

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.antsyferov.tfg.R
import com.tfg.domain.models.ui.Publication
import java.util.Calendar
import java.util.Date

@Composable
fun AddPublication(
    context: Context,
    isLoading: Boolean,
    onSaveButtonClick: (Publication) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        if (isLoading)
            Loader()

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            val calendar = Calendar.getInstance()

            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            var reviewDate by remember { mutableStateOf(Date()) }
            var finalSubmitDate by remember { mutableStateOf(Date()) }
            var completionDate by remember { mutableStateOf(Date()) }

            val isSaveEnabled = title.isNotEmpty() && description.isNotEmpty()

            val reviewDatePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    reviewDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time

                }, currentYear, currentMonth, currentDay
            )
            val finalSubmitDatePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    finalSubmitDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time

                }, currentYear, currentMonth, currentDay
            )
            val completionDatePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    completionDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time

                }, currentYear, currentMonth, currentDay
            )

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.add_publication_name)) },
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.add_publication_description)) },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )



            TextField(
                value = reviewDate.toString(),
                onValueChange = {  },
                label = { Text(stringResource(id = R.string.add_publication_review)) },
                enabled = false,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                reviewDatePickerDialog.show()
            }) {
                Text(text = stringResource(id = R.string.action_calendar), modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

            TextField(
                value = finalSubmitDate.toString(),
                onValueChange = {  },
                label = { Text(stringResource(id = R.string.add_publication_submit)) },
                enabled = false,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                finalSubmitDatePickerDialog.show()
            }) {
                Text(text = stringResource(id = R.string.action_calendar), modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

            TextField(
                value = completionDate.toString(),
                onValueChange = {  },
                label = { Text(stringResource(id = R.string.add_publication_completion)) },
                enabled = false,
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                completionDatePickerDialog.show()
            }) {
                Text(text = stringResource(id = R.string.action_calendar), modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSaveButtonClick.invoke(
                    Publication(
                        title = title,
                        description = description,
                        reviewDate = reviewDate,
                        finalSubmitDate = finalSubmitDate,
                        completionDate = completionDate
                    )
                ) },
                enabled = isSaveEnabled && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.action_save), modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp))
            }

        }
    }

}