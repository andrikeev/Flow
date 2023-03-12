package flow.network.dto

import flow.network.dto.error.FlowError
import kotlinx.serialization.Serializable

@Serializable
sealed interface ResultDto<out T> {
    data class Data<T>(val value: T) : ResultDto<T>
    data class Error(val cause: FlowError) : ResultDto<Nothing>

    companion object {
        val Success: ResultDto<Unit> = Data(Unit)
    }
}
