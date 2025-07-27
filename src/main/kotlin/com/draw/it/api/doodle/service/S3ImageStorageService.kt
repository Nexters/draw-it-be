package com.draw.it.api.doodle.service

import com.draw.it.api.doodle.properties.S3Properties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URI
import java.util.*

private val log = KotlinLogging.logger {}

@Service
@Primary
@EnableConfigurationProperties(S3Properties::class)
class S3ImageStorageService(
    private val s3Properties: S3Properties
) : ImageStorageService {

    private val s3Client: S3Client by lazy {
        val credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(s3Properties.accessKey, s3Properties.secretKey)
        )

        val clientBuilder = S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(s3Properties.region))
            .endpointOverride(URI.create(s3Properties.endpoint))

        clientBuilder.build()
    }

    override fun uploadImage(image: MultipartFile): String {
        val fileName = generateFileName(image.originalFilename)
        
        try {
            val putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName)
                .key(fileName)
                .contentType(image.contentType)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build()

            val requestBody = RequestBody.fromInputStream(
                image.inputStream,
                image.size
            )

            s3Client.putObject(putObjectRequest, requestBody)
            
            val imageUrl = buildImageUrl(fileName)
            log.info { "Successfully uploaded image to S3: $imageUrl" }
            
            return imageUrl
        } catch (e: Exception) {
            log.error(e) { "Failed to upload image to S3: ${e.message}" }
            throw RuntimeException("Failed to upload image to S3", e)
        }
    }

    private fun generateFileName(originalFilename: String?): String {
        val extension = originalFilename?.substringAfterLast('.') ?: "jpg"
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        return "images/${s3Properties.phase}/$timestamp-$uuid.$extension"
    }

    private fun buildImageUrl(fileName: String): String {
        return "${s3Properties.endpoint}/${s3Properties.bucketName}/$fileName"
    }
}
