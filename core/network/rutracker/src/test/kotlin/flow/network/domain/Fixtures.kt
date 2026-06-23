package flow.network.domain

/** Loads a real rutracker HTML page saved under `src/test/resources/fixtures/`. */
internal object Fixtures {
    fun load(name: String): String =
        requireNotNull(Fixtures::class.java.getResourceAsStream("/fixtures/$name")) {
            "fixture not found: /fixtures/$name"
        }.bufferedReader().use { it.readText() }
}
