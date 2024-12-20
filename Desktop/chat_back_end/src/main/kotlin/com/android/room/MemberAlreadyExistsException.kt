package com.android.room

class MemberAlreadyExistsException : Exception(
    "There is already a member with this user name in the room."
)