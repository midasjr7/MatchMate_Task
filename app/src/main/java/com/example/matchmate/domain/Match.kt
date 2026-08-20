package com.example.matchmate.domain

data class Match(
    val id: String,
    val name: String,
    val age: Int,
    val location: String,
    val email: String,
    val phone: String,
    val imageUrl: String,
    val status: MatchStatus,
)

enum class MatchStatus { PENDING, ACCEPTED, DECLINED }
