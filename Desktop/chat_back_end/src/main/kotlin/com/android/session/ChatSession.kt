package com.android.session

import kotlinx.serialization.Serializable

@Serializable
data class ChatSession(
    val userName: String, val session: String
)
