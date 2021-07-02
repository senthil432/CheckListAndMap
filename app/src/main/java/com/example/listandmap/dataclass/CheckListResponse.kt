package com.example.listandmap.dataclass

data class CheckListResponse(
    val userId: Int, val id: Int,
    val title: String,
    val body: String
)