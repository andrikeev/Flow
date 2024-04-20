package flow.designsystem.drawables

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.LastPage
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FiberNew
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Filter1
import androidx.compose.material.icons.outlined.Filter2
import androidx.compose.material.icons.outlined.Filter3
import androidx.compose.material.icons.outlined.Filter4
import androidx.compose.material.icons.outlined.Filter5
import androidx.compose.material.icons.outlined.Filter6
import androidx.compose.material.icons.outlined.Filter7
import androidx.compose.material.icons.outlined.Filter8
import androidx.compose.material.icons.outlined.Filter9
import androidx.compose.material.icons.outlined.Filter9Plus
import androidx.compose.material.icons.outlined.FirstPage
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LinkOff
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.SwitchAccount
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import flow.designsystem.R

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}

object FlowIcons {
    private val MaterialIcons = Icons.Outlined
    private val MaterialIconsAutoMirrored = Icons.AutoMirrored.Outlined

    val Account: Icon = Icon.ImageVectorIcon(MaterialIcons.SwitchAccount)
    val Add: Icon = Icon.ImageVectorIcon(MaterialIcons.Add)
    val AppIcon: Icon = Icon.DrawableResourceIcon(R.drawable.ic_notification)
    val Author: Icon = Icon.ImageVectorIcon(MaterialIcons.PersonSearch)
    val BackArrow: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.ArrowBack)
    val Blocked: Icon = Icon.ImageVectorIcon(MaterialIcons.LinkOff)
    val BookmarkChecked: Icon = Icon.ImageVectorIcon(MaterialIcons.Bookmark)
    val BookmarkUnchecked: Icon = Icon.ImageVectorIcon(MaterialIcons.BookmarkBorder)
    val Bookmarks: Icon = Icon.ImageVectorIcon(MaterialIcons.Bookmarks)
    val Captcha: Icon = Icon.ImageVectorIcon(MaterialIcons.Abc)
    val ChevronRight: Icon = Icon.ImageVectorIcon(MaterialIcons.ChevronRight)
    val Clear: Icon = Icon.ImageVectorIcon(MaterialIcons.Clear)
    val Comment: Icon = Icon.ImageVectorIcon(MaterialIcons.Edit)
    val Connected: Icon = Icon.ImageVectorIcon(MaterialIcons.Link)
    val Delete: Icon = Icon.ImageVectorIcon(Icons.Filled.Delete)
    val DropDownExpand: Icon = Icon.ImageVectorIcon(MaterialIcons.ArrowDropDown)
    val Expand: Icon = Icon.ImageVectorIcon(MaterialIcons.ExpandMore)
    val Favorite: Icon = Icon.ImageVectorIcon(MaterialIcons.Favorite)
    val FavoriteChecked: Icon = Icon.ImageVectorIcon(MaterialIcons.Favorite)
    val FavoriteUnchecked: Icon = Icon.ImageVectorIcon(MaterialIcons.FavoriteBorder)
    val FileDownloadDone: Icon = Icon.ImageVectorIcon(MaterialIcons.FileDownloadDone)
    val FileSize: Icon = Icon.DrawableResourceIcon(R.drawable.ic_folder_download)
    val FirstPage: Icon = Icon.ImageVectorIcon(MaterialIcons.FirstPage)
    val Forum: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.ListAlt)
    val History: Icon = Icon.ImageVectorIcon(MaterialIcons.History)
    val ImagePlaceholder: Icon = Icon.ImageVectorIcon(MaterialIcons.ImageNotSupported)
    val InsertSuggest: Icon = Icon.ImageVectorIcon(MaterialIcons.Edit)
    val LastPage: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.LastPage)
    val Leeches: Icon = Icon.ImageVectorIcon(MaterialIcons.FileDownload)
    val Logout: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.Logout)
    val Menu: Icon = Icon.ImageVectorIcon(MaterialIcons.Menu)
    val NewBadge: Icon = Icon.ImageVectorIcon(MaterialIcons.FiberNew)
    val NextPage: Icon = Icon.ImageVectorIcon(MaterialIcons.ChevronRight)
    val NoInternet: Icon = Icon.DrawableResourceIcon(R.drawable.ic_no_internet)
    val NotSelected: Icon = Icon.ImageVectorIcon(MaterialIcons.RadioButtonUnchecked)
    val Notifications: Icon = Icon.ImageVectorIcon(MaterialIcons.NotificationsActive)
    val Password: Icon = Icon.ImageVectorIcon(MaterialIcons.Password)
    val PasswordHidden: Icon = Icon.ImageVectorIcon(MaterialIcons.VisibilityOff)
    val PasswordVisible: Icon = Icon.ImageVectorIcon(MaterialIcons.Visibility)
    val Pin: Icon = Icon.ImageVectorIcon(Icons.Filled.PushPin)
    val PrevPage: Icon = Icon.ImageVectorIcon(MaterialIcons.ChevronLeft)
    val Reload: Icon = Icon.ImageVectorIcon(MaterialIcons.Sync)
    val Remove: Icon = Icon.ImageVectorIcon(MaterialIcons.Remove)
    val ScrollToTop: Icon = Icon.ImageVectorIcon(MaterialIcons.ExpandLess)
    val Search: Icon = Icon.ImageVectorIcon(MaterialIcons.Search)
    val Seeds: Icon = Icon.ImageVectorIcon(MaterialIcons.FileUpload)
    val Selected: Icon = Icon.ImageVectorIcon(MaterialIcons.CheckCircleOutline)
    val Share: Icon = Icon.ImageVectorIcon(MaterialIcons.Share)
    val StarEmpty: Icon = Icon.ImageVectorIcon(MaterialIcons.StarBorder)
    val StarFull: Icon = Icon.ImageVectorIcon(MaterialIcons.Star)
    val StarHalf: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.StarHalf)
    val Storage: Icon = Icon.ImageVectorIcon(MaterialIcons.SdStorage)
    val Topics: Icon = Icon.ImageVectorIcon(MaterialIcons.Forum)
    val Unpin: Icon = Icon.ImageVectorIcon(MaterialIcons.PushPin)
    val Updating: Icon = Icon.ImageVectorIcon(MaterialIcons.Sync)
    val Username: Icon = Icon.ImageVectorIcon(MaterialIcons.AccountCircle)

    object TorrentStatus {
        val Approved: Icon = Icon.ImageVectorIcon(MaterialIcons.CheckCircleOutline)
        val Checking: Icon = Icon.ImageVectorIcon(MaterialIcons.RadioButtonUnchecked)
        val Closed: Icon = Icon.ImageVectorIcon(MaterialIcons.Cancel)
        val Consumed: Icon = Icon.ImageVectorIcon(MaterialIcons.Functions)
        val Duplicate: Icon = Icon.ImageVectorIcon(MaterialIcons.ContentCopy)
        val NeedsEdit: Icon = Icon.ImageVectorIcon(MaterialIconsAutoMirrored.HelpOutline)
        val NoDescription: Icon = Icon.ImageVectorIcon(MaterialIcons.ErrorOutline)
    }

    object Filters {
        val NoFilters: Icon = Icon.ImageVectorIcon(MaterialIcons.Tune)
        val Filters1: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter1)
        val Filters2: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter2)
        val Filters3: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter3)
        val Filters4: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter4)
        val Filters5: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter5)
        val Filters6: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter6)
        val Filters7: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter7)
        val Filters8: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter8)
        val Filters9: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter9)
        val Filters9Plus: Icon = Icon.ImageVectorIcon(MaterialIcons.Filter9Plus)
    }
}
