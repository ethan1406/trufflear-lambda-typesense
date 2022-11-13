package com.trufflear.lambda.services

import com.trufflear.lambda.configs.TypesenseFields
import com.trufflear.lambda.triggers.models.IndexAction
import org.typesense.api.Client

class SearchIndexService(
    private val client: Client
) {

    fun upsert(insertAction: IndexAction.Upsert) {
        val document = HashMap<String, Any>()
        document[TypesenseFields.postId] = insertAction.postId
        document[TypesenseFields.caption] = insertAction.caption
        document[TypesenseFields.thumbnailUrl] = insertAction.thumbnailUrl
        document[TypesenseFields.mentions] = insertAction.mentions
        document[TypesenseFields.hashtags] = insertAction.hashtags
        document[TypesenseFields.permalink] = insertAction.permalink
        document[TypesenseFields.createdAtTimeMillis] = insertAction.createdAtTimeMillis

        client.collections(insertAction.email).documents().upsert(document)
    }

    fun deletePost(deleteAction: IndexAction.Delete) {
        client.collections(deleteAction.email).documents(deleteAction.postId).delete()
    }
}