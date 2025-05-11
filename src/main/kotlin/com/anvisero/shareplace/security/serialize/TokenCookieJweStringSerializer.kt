package com.anvisero.shareplace.security.serialize

import com.anvisero.shareplace.security.model.Token
import com.nimbusds.jose.*
import com.nimbusds.jwt.EncryptedJWT
import com.nimbusds.jwt.JWTClaimsSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Function

class TokenCookieJweStringSerializer : Function<Token, String?> {
    private val jweEncrypter: JWEEncrypter?

    private var jweAlgorithm: JWEAlgorithm = JWEAlgorithm.DIR

    private var encryptionMethod: EncryptionMethod = EncryptionMethod.A128GCM

    constructor(jweEncrypter: JWEEncrypter?) {
        this.jweEncrypter = jweEncrypter
    }

    constructor(jweEncrypter: JWEEncrypter?, jweAlgorithm: JWEAlgorithm, encryptionMethod: EncryptionMethod) {
        this.jweEncrypter = jweEncrypter
        this.jweAlgorithm = jweAlgorithm
        this.encryptionMethod = encryptionMethod
    }

    override fun apply(token: Token): String? {
        val jwsHeader = JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
            .keyID(token.id.toString())
            .build()
        val claimsSet = JWTClaimsSet.Builder()
            .jwtID(token.id.toString())
            .subject(token.subject)
            .issueTime(Date.from(token.createdAt))
            .expirationTime(Date.from(token.expiresAt))
            .claim("authorities", token.authorities)
            .build()
        val encryptedJWT = EncryptedJWT(jwsHeader, claimsSet)
        try {
            encryptedJWT.encrypt(this.jweEncrypter)

            return encryptedJWT.serialize()
        } catch (exception: JOSEException) {
            LOGGER.error(exception.message, exception)
        }

        return null
    }

    fun setJweAlgorithm(jweAlgorithm: JWEAlgorithm) {
        this.jweAlgorithm = jweAlgorithm
    }

    fun setEncryptionMethod(encryptionMethod: EncryptionMethod) {
        this.encryptionMethod = encryptionMethod
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TokenCookieJweStringSerializer::class.java)
    }
}