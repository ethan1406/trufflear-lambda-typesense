package com.trufflear.lambda.triggers.models

sealed class IndexAction(
    open val postId: String,
    open val email: String
) {
    data class Upsert(
        override val postId: String,
        override val email: String,
        val caption: String,
        val thumbnailUrl: String,
        val mentions: String,
        val hashtags: String,
        val permalink: String,
        val createdAtTimeMillis: Long
    ): IndexAction(postId, email)

    data class Delete(
        override val postId: String,
        override val email: String,
    ): IndexAction(postId, email)
}

enum class TriggerAction {
    INSERT,
    UPDATE,
    DELETE;
}