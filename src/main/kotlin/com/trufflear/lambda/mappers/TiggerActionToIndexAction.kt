package com.trufflear.lambda.mappers

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.trufflear.lambda.configs.TriggerConfigs
import com.trufflear.lambda.services.StorageService
import com.trufflear.lambda.triggers.models.TriggerAction
import com.trufflear.lambda.triggers.models.IndexAction
import com.trufflear.lambda.util.dateFormat
import java.text.ParseException

internal fun toIndexAction(
    input: Map<String, String>,
    storageService: StorageService,
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
            storageService = storageService,
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
    storageService: StorageService,
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

    val thumbnailObjectKey = input[TriggerConfigs.thumbnailObjectKey] ?: run {
        logger.log("missing thumbnail object key")
        return null
    }

    val thumbnailUrl = storageService.getUrl(thumbnailObjectKey)

    val createdAtTimeStamp = input[TriggerConfigs.createdAtTimeStamp] ?: run {
        logger.log("missing timestamp")
        return null
    }

    val timeMillis = parseDate(createdAtTimeStamp, logger) ?: 0L

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

private fun parseDate(
    createdAtTimeStamp: String,
    logger: LambdaLogger
): Long? =
    try {
        dateFormat.parse(createdAtTimeStamp).time
    } catch (e: ParseException) {
        logger.log("parse date error: $e")
        null
    }
