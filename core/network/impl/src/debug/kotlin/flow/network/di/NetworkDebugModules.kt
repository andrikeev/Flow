package flow.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Debug-only Koin modules: adds the Chucker HTTP inspector as an interceptor, collected
 * by the OkHttpClient definition in networkModule via getAll<Interceptor>().
 */
fun networkDebugModules(): List<Module> = listOf(
    module {
        single<Interceptor> { ChuckerInterceptor(get<Context>()) }
    },
)
