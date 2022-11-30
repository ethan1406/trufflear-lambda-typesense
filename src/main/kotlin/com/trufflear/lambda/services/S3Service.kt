package com.trufflear.lambda.services

import com.amazonaws.AmazonServiceException
import com.amazonaws.SdkClientException
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils
import com.trufflear.lambda.frameworks.Result
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.URL

private const val s3Bucket = "trufflear"
private const val s3Region = "us-west-1"

class S3Service(
    val client: AmazonS3,
    private val logger: LambdaLogger
): StorageService {

    override fun getUrl(objectKey: String): String = "https://$s3Bucket.s3.$s3Region.amazonaws.com/$objectKey"

    override fun uploadImageToKey(imageUrl: String, objectKey: String): Result<Unit, Unit> =
        try {
            logger.log("uploading image to key: $objectKey")

            val contents = IOUtils.toByteArray(URL(imageUrl).openStream())
            val stream = ByteArrayInputStream(contents)
            val metadata = ObjectMetadata().apply {
                contentType = contentType
                contentLength = contents.size.toLong()
            }

            client.putObject(s3Bucket, objectKey, stream, metadata)

            stream.close()

            Result.Success(Unit)
        } catch (e: AmazonServiceException) {
            logger.log("error uploading image to key: $objectKey Error: $e" )
            Result.Error(Unit)
        } catch (e: SdkClientException) {
            logger.log("error uploading image to key: $objectKey Error: $e" )
            Result.Error(Unit)
        } catch (e: IOException) {
            logger.log("error uploading image to key: $objectKey Error: $e" )
            Result.Error(Unit)
        } catch (e: Exception) {
            logger.log("error uploading image to key: $objectKey Error: $e" )
            Result.Error(Unit)
        }

    override fun deleteObject(objectKey: String): Result<Unit, Unit> =
        try {
            logger.log("deleting object with key: $objectKey")

            client.deleteObject(s3Bucket, objectKey)

            Result.Success(Unit)
        } catch (e: AmazonServiceException) {
            logger.log("error deleting object with key: $objectKey Error $e")
            Result.Error(Unit)
        } catch (e: SdkClientException) {
            logger.log("error deleting object with key: $objectKey Error $e")
            Result.Error(Unit)
        } catch (e: Exception) {
            logger.log("error deleting object with key: $objectKey Error $e")
            Result.Error(Unit)
        }
}