package com.trufflear.lambda

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.trufflear.lambda.mappers.toIndexAction
import com.trufflear.lambda.services.S3Service
import com.trufflear.lambda.services.SearchIndexService
import com.trufflear.lambda.services.StorageService
import com.trufflear.lambda.triggers.models.IndexAction
import org.typesense.api.Client
import org.typesense.api.Configuration
import org.typesense.resources.Node
import java.time.Duration


open class Handler : RequestHandler<Map<String, String>, String>{

    private val searchIndexService = SearchIndexService(getTypeSenseClient())

    override fun handleRequest(input: Map<String, String>, context: Context): String {
        val logger = context.logger

        val storageService: StorageService = S3Service(getS3Client(), logger)

        val triggerAction = toIndexAction(input, storageService, logger)

        triggerAction?.let { action ->
            when (action) {
                is IndexAction.Insert -> searchIndexService.insert(action)
                is IndexAction.Update -> searchIndexService.update(action)
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

private fun getS3Client() = AmazonS3ClientBuilder.standard()
    .withRegion(Regions.US_WEST_1)
    .withCredentials(
        AWSStaticCredentialsProvider(
            BasicAWSCredentials(
                System.getenv("S3_ACCESS_KEY"),
                System.getenv("S3_SECRET_KEY")
            )
        )
    )
    .build()