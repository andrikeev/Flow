package flow.rating

sealed interface RatingAction {
    data object AskLaterClick : RatingAction
    data object DismissClick : RatingAction
    data object NeverAskAgainClick : RatingAction
    data object RatingClick : RatingAction
}
