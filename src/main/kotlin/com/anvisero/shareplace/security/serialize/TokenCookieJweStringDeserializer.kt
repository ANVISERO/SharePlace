package com.anvisero.shareplace.security.serialize

import com.anvisero.shareplace.security.model.Token
import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWEDecrypter
import com.nimbusds.jwt.EncryptedJWT
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.ParseException
import java.util.*
import java.util.function.Function

class TokenCookieJweStringDeserializer(private val jweDecrypter: JWEDecrypter) : Function<String?, Token?> {

    private val log: Logger = LoggerFactory.getLogger(TokenCookieJweStringDeserializer::class.java)

    override fun apply(string: String?): Token? {
        try {
            val encryptedJWT = EncryptedJWT.parse(string)
            encryptedJWT.decrypt(this.jweDecrypter)
            val claimsSet = encryptedJWT.getJWTClaimsSet()
            return Token(
                UUID.fromString(claimsSet.jwtid), claimsSet.subject,
                claimsSet.getStringListClaim("authorities"),
                claimsSet.issueTime.toInstant(),
                claimsSet.expirationTime.toInstant()
            )
        } catch (exception: ParseException) {
            log.error(exception.message, exception)
        } catch (exception: JOSEException) {
            log.error(exception.message, exception)
        }

        return null
    }
}