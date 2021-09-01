package me.rutrackersearch.data.sync

import me.rutrackersearch.domain.entity.settings.SyncPeriod
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal val SyncPeriod.repeatIntervalMillis: Long
    get() = when (this) {
        SyncPeriod.OFF -> Duration.ZERO
        SyncPeriod.HOUR -> 1.hours
        SyncPeriod.SIX_HOURS -> 6.hours
        SyncPeriod.TWELVE_HOURS -> 12.hours
        SyncPeriod.DAY -> 1.days
        SyncPeriod.WEEK -> 7.days
    }.inWholeMilliseconds

internal val SyncPeriod.flexIntervalMillis: Long
    get() = when (this) {
        SyncPeriod.OFF -> Duration.ZERO
        SyncPeriod.HOUR -> 15.minutes
        SyncPeriod.SIX_HOURS -> 1.hours
        SyncPeriod.TWELVE_HOURS -> 2.hours
        SyncPeriod.DAY -> 6.days
        SyncPeriod.WEEK -> 1.days
    }.inWholeMilliseconds
