package com.trufflear.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.trufflear.lambda.mappers.toIndexAction
import com.trufflear.lambda.services.SearchIndexService
import com.trufflear.lambda.triggers.models.IndexAction
import org.typesense.api.Client
import org.typesense.api.Configuration
import org.typesense.resources.Node
import java.time.Duration


open class Handler : RequestHandler<Map<String, String>, String>{

    private val searchIndexService = SearchIndexService(getTypeSenseClient())

    private fun getTypeSenseClient(): Client {
        val nodes = listOf(
            Node(
                System.getenv("TYPESENSE_PROTOCOL") ?: "https",  // For Typesense Cloud use https
                System.getenv("TYPESENSE_HOST")?: "search.trufflear.com",  // For Typesense Cloud use xxx.a1.typesense.net
                System.getenv("TYPESENSE_PORT") ?:"8108" // For Typesense Cloud use 443
            )
        )

        val configuration = Configuration(nodes, Duration.ofSeconds(2), System.getenv("TYPESENSE_ADMIN_API_KEY") ?: "")
        return Client(configuration)
    }

    override fun handleRequest(input: Map<String, String>, context: Context): String {
        val logger = context.logger

        val triggerAction = toIndexAction(input, logger)

        triggerAction?.let { action ->
            when (action) {
                is IndexAction.Upsert -> searchIndexService.upsert(action)
                is IndexAction.Delete -> searchIndexService.deletePost(action)
            }
        } ?: run {
            val errorMessage = "incorrect trigger data"
            logger.log(errorMessage)
            throw IllegalArgumentException(errorMessage)
        }

        return "200 OK"
    }
}