package com.trufflear.lambda.services

interface StorageService {
    fun getUrl(objectKey: String): String
}