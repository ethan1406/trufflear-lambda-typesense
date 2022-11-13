package com.trufflear.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.GsonBuilder
import com.trufflear.lambda.configs.TypesenseFields
import com.trufflear.lambda.configs.apiKey
import com.trufflear.lambda.mappers.convertInputToDataResponse
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

        triggerResponse?.let {
            val client = getTypeSenseClient()
            val collectionSchema = CollectionSchema()
            collectionSchema.name(triggerResponse.email).fields(
                listOf(
                    Field().name(TypesenseFields.postId).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.caption).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.thumbnailUrl).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.mentions).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.hashtags).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.permalink).type(FieldTypes.STRING),
                    Field().name(TypesenseFields.createdAtTimeMilli).type(FieldTypes.INT64)
                )
            ).defaultSortingField(TypesenseFields.createdAtTimeMilli)

            client.collections().create(collectionSchema)
        } ?: run {
            val errorMessage = "incorrect trigger data"
            logger.log(errorMessage)
            throw java.lang.IllegalArgumentException(errorMessage)
        }

        return "200 OK"
    }
}