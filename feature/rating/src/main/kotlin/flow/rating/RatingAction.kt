package flow.rating

sealed interface RatingAction {
    object AskLaterClick : RatingAction
    object DismissClick : RatingAction
    object NeverAskAgainClick : RatingAction
    object RatingClick : RatingAction
}
