package com.pocketdimen.firebasepractice.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pocketdimen.firebasepractice.screen.auth.Const
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel(
    private val auth: FirebaseAuth,
    private val realtime: FirebaseDatabase,
): ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    init {
        auth.currentUser?.let {
            getFriendList(it.uid)
        }
    }

    fun onEvent(event: ChatUiEvent) {
        when(event) {
            is ChatUiEvent.FindUser -> findUser(event.email)
        }
    }

    private fun findUser(email: String) {
        Log.d("ChatViewModel", "findUser: $email")
        realtime.getReference(Const.Users.value)
            .orderByChild(Const.Email.value)
            .equalTo(email)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (child in snapshot.children) {
                                val uid = child.key
                                val userData = child.value as Map<*, *>
                                val displayName = userData[Const.DisplayName.value] as String
                                auth.currentUser?.let {currentUser ->
                                    uid?.let {
                                        addUserToFriendList(
                                            myUid = currentUser.uid,
                                            uid = it,
                                            displayName = displayName,
                                            email = email
                                        )
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                }
            )
    }
    private fun addUserToFriendList(
        myUid: String,
        uid: String,
        displayName: String,
        email: String
    ) {
        realtime.getReference(Const.MyFriends.value)
            .child(myUid)
            .child(uid)
            .setValue(
            mapOf(
                Const.DisplayName.value to displayName,
                Const.Email.value to email
            )
        )
    }

    private fun getFriendList(uid: String) {
        realtime.getReference(Const.MyFriends.value)
            .child(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val friendList = mutableListOf<Pair<String, String>>()
                    for (child in it.children) {
                        val friendData = child.value as Map<*, *>
                        val displayName = friendData[Const.DisplayName.value] as String
                        val email = friendData[Const.Email.value] as String
                        friendList.add(Pair(child.key.toString(), displayName))
                    }
                    _state.update { currentState ->
                        currentState.copy(
                            myFriends = friendList
                        )
                    }
                }
            }
    }

}

data class ChatUiState(
    // a pair defines a users uid and displayName
    val myFriends: List<Pair<String, String>> = emptyList()
)

sealed class ChatUiEvent {
    data class FindUser(val email: String): ChatUiEvent()
}