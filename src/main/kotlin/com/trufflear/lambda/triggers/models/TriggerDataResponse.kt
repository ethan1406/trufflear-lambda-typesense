package com.trufflear.lambda.triggers.models

data class TriggerDataResponse(
    val postId: String,
    val action: Action,
    val caption: String,
    val thumbnailUrl: String,
    val mentions: String,
    val hashtags: String,
    val permalink: String,
    val email: String,
    val createdAtTimeMillis: Long
)

enum class Action {
    INSERT,
    UPDATE,
    DELETE;
}