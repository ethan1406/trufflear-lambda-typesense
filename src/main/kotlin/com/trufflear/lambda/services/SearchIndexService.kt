package com.trufflear.lambda.services

import com.trufflear.lambda.configs.SearchIndexFields
import com.trufflear.lambda.triggers.models.IndexAction
import org.typesense.api.Client

class SearchIndexService(
    private val client: Client
) {

    fun insert(insertAction: IndexAction.Insert) {
        val document = HashMap<String, Any>()
        document[SearchIndexFields.postId] = insertAction.postId
        document[SearchIndexFields.caption] = insertAction.caption
        document[SearchIndexFields.thumbnailUrl] = insertAction.thumbnailUrl
        document[SearchIndexFields.mentions] = insertAction.mentions
        document[SearchIndexFields.hashtags] = insertAction.hashtags
        document[SearchIndexFields.permalink] = insertAction.permalink
        document[SearchIndexFields.createdAtTimeMillis] = insertAction.createdAtTimeMillis

        client.collections(insertAction.email).documents().create(document)
    }

    fun update(insertAction: IndexAction.Update) {
        val document = HashMap<String, Any>()
        document[SearchIndexFields.caption] = insertAction.caption
        document[SearchIndexFields.mentions] = insertAction.mentions
        document[SearchIndexFields.hashtags] = insertAction.hashtags

        client.collections(insertAction.email).documents(insertAction.postId).update(document)
    }


    fun deletePost(deleteAction: IndexAction.Delete) {
        client.collections(deleteAction.email).documents(deleteAction.postId).delete()
    }
}