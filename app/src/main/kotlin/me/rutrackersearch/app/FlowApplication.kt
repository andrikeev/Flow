package me.rutrackersearch.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@HiltAndroidApp
class FlowApplication : Application()
