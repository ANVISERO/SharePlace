//package com.anvisero.shareplace.config
//
//import com.amazonaws.auth.AWSCredentials
//import com.amazonaws.auth.AWSStaticCredentialsProvider
//import com.amazonaws.auth.BasicAWSCredentials
//import com.amazonaws.client.builder.AwsClientBuilder
//import com.amazonaws.services.s3.AmazonS3
//import com.amazonaws.services.s3.AmazonS3ClientBuilder
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.PropertySource
//
//@Configuration
//@PropertySource(value = ["classpath:application.yml"])
//data class ObjectStorageConfig(
//    @Value("\${cloud.yandex.s3.bucket-name}")
//    val bucketName: String? = null,
//    @Value("\${cloud.yandex.s3.credentials.access-key-id}")
//    val accessKeyId: String? = null,
//    @Value("\${cloud.yandex.s3.credentials.secret-key}")
//    val secretAccessKey: String? = null,
//    @Value("\${cloud.yandex.s3.service-endpoint}")
//    val serviceEndpoint: String? = null,
//    @Value("\${cloud.yandex.s3.region}")
//    val signingRegion: String? = null
//) {
//    @Bean
//    fun generateS3Client(): AmazonS3 {
//        val credentials: AWSCredentials = BasicAWSCredentials(accessKeyId, secretAccessKey)
//        return AmazonS3ClientBuilder.standard()
//            .withEndpointConfiguration(
//                AwsClientBuilder.EndpointConfiguration(
//                    serviceEndpoint,
//                    signingRegion
//                )
//            )
//            .withCredentials(AWSStaticCredentialsProvider(credentials))
//            .build()
//    }
//}