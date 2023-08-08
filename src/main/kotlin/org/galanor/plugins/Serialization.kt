package org.galanor.plugins

import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.server.response.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import javax.print.attribute.standard.Compression

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    routing {
        val cacheBaseUrl = "https://galanor.s3.amazonaws.com/caches"
        val clientBaseUrl = "https://galanor.s3.amazonaws.com/clients"
        get("/") {
            val version = "1.2.8"
            call.respond(Version(1.5, 1.28, 1.0, true,
                "$cacheBaseUrl/galanor-cache.zip",
                "$clientBaseUrl/galanor-$version-obfuscated-Live.jar"))
        }
    }
}

class Version(val launcherVersion: Double,
              val clientVersion: Double,
              val cacheVersion: Double,
              val downloadCache: Boolean,
              val cacheDownloadUrl: String,
              val clientDownloadUrl: String)
