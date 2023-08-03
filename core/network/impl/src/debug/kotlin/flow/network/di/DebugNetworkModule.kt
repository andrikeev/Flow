package flow.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
object DebugNetworkModule {
    @Provides
    @IntoSet
    fun chuckerInterceptor(@ApplicationContext context: Context): Interceptor {
        return ChuckerInterceptor(context)
    }
}
