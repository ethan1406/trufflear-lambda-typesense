package com.trufflear.lambda.services

import com.trufflear.lambda.frameworks.Result

interface StorageService {
    fun getUrl(objectKey: String): String

    fun uploadImageToKey(imageUrl: String, objectKey: String): Result<Unit, Unit>

    fun deleteObject(objectKey: String): Result<Unit, Unit>
}