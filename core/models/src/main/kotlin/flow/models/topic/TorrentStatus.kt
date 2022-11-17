package flow.models.topic

enum class TorrentStatus {
    DUPLICATE,
    NOT_APPROVED,
    CHECKING,
    APPROVED,
    NEED_EDIT,
    CLOSED,
    NO_DESCRIPTION,
    CONSUMED,
}

fun TorrentStatus?.isValid(): Boolean = when (this) {
    TorrentStatus.CLOSED,
    TorrentStatus.CONSUMED -> false

    else -> true
}
