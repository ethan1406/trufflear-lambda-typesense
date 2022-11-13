package com.trufflear.lambda.mappers

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.trufflear.lambda.configs.TriggerConfigs
import com.trufflear.lambda.triggers.models.Action
import com.trufflear.lambda.triggers.models.TriggerDataResponse
import java.text.SimpleDateFormat


internal fun convertInputToDataResponse(
    input: Map<String, String>,
    logger: LambdaLogger
): TriggerDataResponse? {
    val id = input[TriggerConfigs.postId] ?: run {
        logger.log("missing id")
        return null
    }

    val actionString = input[TriggerConfigs.action] ?: run {
        logger.log("missing action")
        return null
    }
    val action = Action.values().firstOrNull {it.name == actionString} ?: run {
        logger.log("unknown action")
        return null
    }

    val caption = input[TriggerConfigs.caption] ?: run {
        logger.log("missing caption")
        return null
    }
    val thumbnailUrl = input[TriggerConfigs.thumbnailUrl] ?: run {
        logger.log("missing thumbnail url")
        return null
    }
    val mentions = input[TriggerConfigs.mentions] ?: run {
        logger.log("missing mentions")
        return null
    }
    val hashtags = input[TriggerConfigs.hashtags] ?: run {
        logger.log("missing hashtags")
        return null
    }
    val permalink = input[TriggerConfigs.permalink] ?: run {
        logger.log("missing permalink")
        return null
    }
    val email = input[TriggerConfigs.email] ?: run {
        logger.log("missing email")
        return null
    }
    val createdAtTimeStamp = input[TriggerConfigs.createdAtTimeStamp] ?: run {
        logger.log("missing timestamp")
        return null
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val result = runCatching {
        dateFormat.parse(createdAtTimeStamp).time
    }.onFailure {
        logger.log("parse date error: $it")
    }

    val timeMillis = result.getOrDefault(0L)

    return TriggerDataResponse(
        postId = id,
        action = action,
        caption = caption,
        thumbnailUrl = thumbnailUrl,
        mentions = mentions,
        hashtags = hashtags,
        permalink = permalink,
        email = email,
        createdAtTimeMillis = timeMillis
    )
}