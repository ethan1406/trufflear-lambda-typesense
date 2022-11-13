package com.trufflear.lambda.triggers.models

sealed class TriggerAction(
    open val postId: String,
    open val email: String
) {
    data class Insert(
        override val postId: String,
        override val email: String,
        val caption: String,
        val thumbnailUrl: String,
        val mentions: String,
        val hashtags: String,
        val permalink: String,
        val createdAtTimeMillis: Long
    ): TriggerAction(postId, email)

    data class Delete(
        override val postId: String,
        override val email: String,
    ): TriggerAction(postId, email)

    data class Update(
        override val postId: String,
        override val email: String,
        val caption: String,
        val mentions: String,
        val hashtags: String,
        val permalink: String
    ): TriggerAction(postId, email)
}

enum class Action {
    INSERT,
    UPDATE,
    DELETE;
}