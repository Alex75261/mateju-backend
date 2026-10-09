package com.mateju

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun `health endpoint confirms that the API is available`() = testApplication {
        application { module() }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "\"status\":\"ok\"")
    }

    @Test
    fun `valid account receives safe role-based response`() = testApplication {
        application { module() }

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"ana.estudiante","password":"Mateju2026!"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.bodyAsText()
        assertContains(payload, "\"id\":\"stu-001\"")
        assertContains(payload, "\"displayName\":\"Ana López\"")
        assertContains(payload, "\"role\":\"STUDENT\"")
        assertEquals(false, payload.contains("password"))
    }

    @Test
    fun `invalid credentials return unauthorized`() = testApplication {
        application { module() }

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"username":"ana.estudiante","password":"incorrecta"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertContains(response.bodyAsText(), "Credenciales inválidas")
    }

    @Test
    fun `malformed login body returns bad request`() = testApplication {
        application { module() }

        val response = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{not-json")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertContains(response.bodyAsText(), "JSON válido")
    }
}
