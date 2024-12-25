package com.pocketdimen.firebasepractice.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun HomeScreen(
    friendList: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    onEvent: (ChatUiEvent) -> Unit = {}
) {
    var dialogState by remember {
        mutableStateOf(false)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    dialogState = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Connect With Friend"
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .then(modifier)
        ) {
            LazyColumn {
                items(friendList.size) { index ->
                    UserCardComponent(
                        name = friendList[index].second
                    )
                }
            }
        }
    }


    if (dialogState) {
        var email by remember {
            mutableStateOf("")
        }
        Dialog(
            onDismissRequest = {}
        ) {
            Column {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    label = {
                        Text(text = "Email")
                    }
                )
                Button(
                    onClick = {
                        onEvent(ChatUiEvent.FindUser(email))
                        dialogState = false
                    }
                ) {
                    Text(text = "Connect")
                }
            }

        }
    }

}

@Composable
fun UserCardComponent(
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(modifier)
            .clickable {
                onClick()
            }
    ) {
        Text(name)
    }
}