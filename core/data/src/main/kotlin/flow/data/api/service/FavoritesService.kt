package flow.data.api.service

import flow.models.topic.Topic

interface FavoritesService {
    suspend fun getFavorites(): List<Topic>
    suspend fun add(id: String)
    suspend fun remove(id: String)
}
