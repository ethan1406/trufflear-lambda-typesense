package com.trufflear.lambda.services

import com.trufflear.lambda.configs.TypesenseFields
import com.trufflear.lambda.triggers.models.TriggerAction
import org.typesense.api.Client

class SearchIndexService(
    private val client: Client
) {

    fun updatePost(updateAction: TriggerAction.Update) {
        val document = HashMap<String, Any>()
        document[TypesenseFields.postId] = updateAction.postId
        document[TypesenseFields.caption] = updateAction.caption
        document[TypesenseFields.mentions] = updateAction.mentions
        document[TypesenseFields.hashtags] = updateAction.hashtags
        document[TypesenseFields.permalink] = updateAction.permalink

        client.collections(updateAction.email).documents(updateAction.postId).update(document)
    }

    fun insertPost(insertAction: TriggerAction.Insert) {
        val document = HashMap<String, Any>()
        document[TypesenseFields.postId] = insertAction.postId
        document[TypesenseFields.caption] = insertAction.caption
        document[TypesenseFields.thumbnailUrl] = insertAction.thumbnailUrl
        document[TypesenseFields.mentions] = insertAction.mentions
        document[TypesenseFields.hashtags] = insertAction.hashtags
        document[TypesenseFields.permalink] = insertAction.permalink
        document[TypesenseFields.createdAtTimeMillis] = insertAction.createdAtTimeMillis

        client.collections(insertAction.email).documents().create(document)
    }

    fun deletePost(deleteAction: TriggerAction.Delete) {
        client.collections(deleteAction.email).documents(deleteAction.postId).delete()
    }
}