package com.trufflear.lambda.services

private const val s3Bucket = "trufflear"
private const val s3Region = "us-west-1"

class S3Service: StorageService {

    override fun getUrl(objectKey: String): String = "https://$s3Bucket.s3.$s3Region.amazonaws.com/$objectKey"

}