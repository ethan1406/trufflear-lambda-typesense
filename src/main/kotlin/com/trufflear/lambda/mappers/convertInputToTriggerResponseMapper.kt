package com.trufflear.lambda.mappers

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.trufflear.lambda.configs.TriggerConfigs
import com.trufflear.lambda.triggers.models.TriggerAction
import com.trufflear.lambda.triggers.models.IndexAction
import java.text.SimpleDateFormat


internal fun convertInputToDataResponse(
    input: Map<String, String>,
    logger: LambdaLogger
): IndexAction? {
    val id = input[TriggerConfigs.postId] ?: run {
        logger.log("missing id")
        return null
    }

    val email = input[TriggerConfigs.email] ?: run {
        logger.log("missing email")
        return null
    }

    val actionString = input[TriggerConfigs.action] ?: run {
        logger.log("missing action")
        return null
    }
    val action = TriggerAction.values().firstOrNull {it.name == actionString} ?: run {
        logger.log("unknown action")
        return null
    }
    logger.log("trigger action is $action")

    return when (action) {
        TriggerAction.INSERT, TriggerAction.UPDATE -> convertToUpsertAction(
            input = input,
            id = id,
            email = email,
            logger = logger
        )
        TriggerAction.DELETE -> convertToDeleteAction(
            id = id,
            email = email
        )
    }
}

private fun convertToUpsertAction(
    id: String,
    email: String,
    logger: LambdaLogger,
    input: Map<String, String>
): IndexAction? {
    val caption = input[TriggerConfigs.caption] ?: run {
        logger.log("missing caption")
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

    val createdAtTimeStamp = input[TriggerConfigs.createdAtTimeStamp] ?: run {
        logger.log("missing timestamp")
        return null
    }

    val thumbnailUrl = input[TriggerConfigs.thumbnailUrl] ?: run {
        logger.log("missing thumbnail url")
        return null
    }


    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    val result = runCatching {
        dateFormat.parse(createdAtTimeStamp).time
    }.onFailure {
        logger.log("parse date error: $it")
    }

    val timeMillis = result.getOrDefault(0L)

    return IndexAction.Upsert(
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



private fun convertToDeleteAction(
    id: String,
    email: String,
) = IndexAction.Delete(
        postId = id,
        email = email
    )
