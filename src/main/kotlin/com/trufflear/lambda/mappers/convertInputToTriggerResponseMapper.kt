package com.trufflear.lambda.mappers

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.trufflear.lambda.configs.TriggerConfigs
import com.trufflear.lambda.triggers.models.TriggerDataResponse
import java.text.SimpleDateFormat


internal fun convertInputToDataResponse(
    input: Map<String, String>,
    logger: LambdaLogger
): TriggerDataResponse? {
    val id = input[TriggerConfigs.postId] ?: return null
    val caption = input[TriggerConfigs.caption] ?: return null
    val thumbnailUrl = input[TriggerConfigs.thumbnailUrl] ?: return null
    val mentions = input[TriggerConfigs.mentions] ?: return null
    val hashtags = input[TriggerConfigs.hashtags] ?: return null
    val permalink = input[TriggerConfigs.permalink] ?: return null
    val email = input[TriggerConfigs.email] ?: return null
    val createdAtTimeStamp = input[TriggerConfigs.createdAtTimeStamp] ?: return null

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val result = runCatching {
        dateFormat.parse(createdAtTimeStamp).time
    }.onFailure {
        logger.log("parse data error: $it")
    }

    val timeMillis = result.getOrDefault(0L)

    return TriggerDataResponse(
        postId = id,
        caption = caption,
        thumbnailUrl = thumbnailUrl,
        mentions = mentions,
        hashtags = hashtags,
        permalink = permalink,
        email = email,
        createdAtTimeMillis = timeMillis
    )
}