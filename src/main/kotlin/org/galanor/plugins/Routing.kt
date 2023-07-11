package org.galanor.plugins

import com.google.gson.GsonBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class RequestData(
    val time: ZonedDateTime,
    val uri: String,
    val os: String,
    val ipAddress: String,
    val username: String,
    val stacktrace: String
)

fun Application.configureRouting() {
    routing {
        staticResources("/", "assets")
        get("/report") {
            val userAgent = call.request.headers["User-Agent"]
            if (userAgent == "Telos-CrashReporter") {
                val time = ZonedDateTime.now(ZoneId.of("America/New_York"))
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH-mm-ss.SSS")
                val filename = formatter.format(time)
                val ipAddress = call.request.origin.remoteHost
                val stacktrace = call.request.queryParameters["stacktrace"] ?: ""
                val username = call.request.queryParameters["username"] ?: ""
                val osName = call.request.queryParameters["os"] ?: ""
                val requestData = RequestData(
                    time,
                    call.request.uri,
                    osName,
                    ipAddress,
                    username,
                    stacktrace
                )
                val json = GsonBuilder().setPrettyPrinting().create()
                File("reports/${filename}.json").writeText(json.toJson(requestData))
                call.respond(HttpStatusCode.Accepted)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid User-Agent")
            }
        }
    }
}
