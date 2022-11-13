package com.trufflear.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.GsonBuilder
import com.trufflear.lambda.configs.TypesenseFields
import com.trufflear.lambda.configs.apiKey
import com.trufflear.lambda.mappers.convertInputToDataResponse
import com.trufflear.lambda.triggers.models.Action
import com.trufflear.lambda.triggers.models.TriggerDataResponse
import org.typesense.api.Client
import org.typesense.api.Configuration
import org.typesense.api.FieldTypes
import org.typesense.model.CollectionSchema
import org.typesense.model.Field
import org.typesense.resources.Node
import java.time.Duration


open class Handler : RequestHandler<Map<String, String>, String>{

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private fun getTypeSenseClient(): Client {
        val nodes = listOf(
            Node(
                "https",  // For Typesense Cloud use https
                "search.trufflear.com",  // For Typesense Cloud use xxx.a1.typesense.net
                "8108" // For Typesense Cloud use 443
            )
        )

        val configuration = Configuration(nodes, Duration.ofSeconds(2), apiKey)
        return Client(configuration)
    }

    override fun handleRequest(input: Map<String, String>, context: Context): String {
        val logger = context.logger

        val triggerResponse = convertInputToDataResponse(input, logger)

        triggerResponse?.let { response ->
            val client = getTypeSenseClient()

            when (response.action) {
                Action.INSERT -> insertPost(response, client)
            }
        } ?: run {
            val errorMessage = "incorrect trigger data"
            logger.log(errorMessage)
            throw java.lang.IllegalArgumentException(errorMessage)
        }

        return "200 OK"
    }

    private fun insertPost(
        response: TriggerDataResponse,
        client: Client
    ) {
        val fieldMap = HashMap<String, Any>()
        fieldMap[TypesenseFields.postId] = response.postId
        fieldMap[TypesenseFields.caption] = response.caption
        fieldMap[TypesenseFields.thumbnailUrl] = response.thumbnailUrl
        fieldMap[TypesenseFields.mentions] = response.mentions
        fieldMap[TypesenseFields.hashtags] = response.hashtags
        fieldMap[TypesenseFields.permalink] = response.permalink
        fieldMap[TypesenseFields.createdAtTimeMillis] = response.createdAtTimeMillis

        client.collections(response.email).documents().create(fieldMap)

    }

}