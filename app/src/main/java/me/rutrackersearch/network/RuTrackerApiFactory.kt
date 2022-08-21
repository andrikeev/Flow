package me.rutrackersearch.network

import me.rutrackersearch.network.rutracker.RuTrackerApi

interface RuTrackerApiFactory {
    fun create(): RuTrackerApi
}