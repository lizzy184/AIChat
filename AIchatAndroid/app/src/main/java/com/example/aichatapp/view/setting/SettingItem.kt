package com.example.aichatapp.view.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SettingItem(
     title: String,

   checked: Boolean?=null,
    onCheckedChange:((Boolean)-> Unit)?=null,
     onClick:()-> Unit={}

                ){
    Card(

        modifier =
            Modifier.fillMaxWidth()
                .clickable{onClick()}

    ){


        Row(

            modifier =
                Modifier.padding(16.dp)

        ){


            Text(

                text = title,

                modifier =
                    Modifier.weight(1f)

            )


if (checked!=null) {
    Switch(

        checked = checked,

        onCheckedChange = {
            onCheckedChange?.invoke(it)
        }
    )
}

        }


    }









}