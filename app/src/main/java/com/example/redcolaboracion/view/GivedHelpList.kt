package com.example.redcolaboracion.view

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.redcolaboracion.model.RequestedHelp
import com.example.redcolaboracion.navigation.BottomNavItem
import com.example.redcolaboracion.viewmodel.GivedHelpListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GivedHelpList(viewModel: GivedHelpListViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.readEvent()
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row{
                Text(text = "",
                    modifier = Modifier.width(20.dp)
                )
                Text(text = "Fecha",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
                Text(text = "Categoría",
                    modifier = Modifier
                        .width(80.dp)
                        .padding (horizontal = 2.dp)
                )
                Text(text = "Solicitante",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
                Text(text = "Estado",
                    modifier = Modifier
                        .padding (horizontal = 2.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Divider()

            LazyColumn (
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.uiGivedHelpList.size) { currentGivedHelp ->
                    val givedHelp = viewModel.uiGivedHelpList[currentGivedHelp]
                    GivedHelpRow(givedHelp){ requestedHelpId ->
                        navController.navigate("${BottomNavItem.History.route}/UserInfo/$requestedHelpId")
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun GivedHelpRow(givedHelp: RequestedHelp, onClick:(String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(givedHelp.requestUserId) }
    ) {
        if ( givedHelp.efectiveHelp.toBoolean()) {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fcheck.png?alt=media&token=35c8dc77-7bae-46df-9b0d-d01a1f1a7343"),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 2.dp)
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter("https://firebasestorage.googleapis.com/v0/b/redcolaboracion-7d500.appspot.com/o/images%2Fcross.png?alt=media&token=02c50774-f140-461f-8a01-2e20968a9d62"),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(horizontal = 2.dp)
            )
        }
        Text(
            text = givedHelp.requestDate,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
        Text(
            text = givedHelp.category,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .width(80.dp)
                .padding (horizontal = 2.dp)
        )
        Text(
            text = givedHelp.requestUser,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
        Text(
            text = givedHelp.status,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        )
    }
}
/*
@Preview(showBackground = true)
@Composable
fun PreviewGivedHelpList() {
    GivedHelpList(viewModel = GivedHelpListViewModel())
} */