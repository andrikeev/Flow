package flow.securestorage

import flow.models.settings.Endpoint
import flow.securestorage.model.EndpointConverter
import org.junit.Assert.assertEquals
import org.junit.Test

class EndpointConverterTest {

    @Test
    fun `convert Proxy to json `() {
        assertEquals(
            "{\"type\":\"Proxy\"}",
            with(EndpointConverter) { Endpoint.Proxy.toJson() },
        )
    }

    @Test
    fun `convert Rutracker to json `() {
        assertEquals(
            "{\"type\":\"Rutracker\"}",
            with(EndpointConverter) { Endpoint.Rutracker.toJson() },
        )
    }

    @Test
    fun `convert Mirror to json `() {
        assertEquals(
            "{\"host\":\"example.com\",\"type\":\"Mirror\"}",
            with(EndpointConverter) { Endpoint.Mirror("example.com").toJson() },
        )
    }

    @Test
    fun `parse Proxy from json`() {
        assertEquals(
            Endpoint.Proxy,
            with(EndpointConverter) { fromJson("{\"type\":\"Proxy\"}") },
        )
    }

    @Test
    fun `parse Rutracker from json`() {
        assertEquals(
            Endpoint.Rutracker,
            with(EndpointConverter) { fromJson("{\"type\":\"Rutracker\"}") },
        )
    }

    @Test
    fun `parse Mirror from json`() {
        assertEquals(
            Endpoint.Mirror("example.com"),
            with(EndpointConverter) { fromJson("{\"host\":\"example.com\",\"type\":\"Mirror\"}") },
        )
    }
}
