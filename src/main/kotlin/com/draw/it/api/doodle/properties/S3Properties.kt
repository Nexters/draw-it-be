package com.draw.it.api.doodle.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "aws.s3")
data class S3Properties @ConstructorBinding constructor(
    val accessKey: String,
    val secretKey: String,
    val region: String,
    val bucketName: String,
    val endpoint: String,
    val phase: String
)
