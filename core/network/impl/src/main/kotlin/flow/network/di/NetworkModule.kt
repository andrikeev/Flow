package flow.network.di

import android.net.TrafficStats
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import flow.network.api.NetworkApi
import flow.network.data.NetworkApiRepository
import flow.network.data.NetworkApiRepositoryImpl
import flow.network.impl.SwitchingNetworkApi
import okhttp3.OkHttpClient
import java.net.InetAddress
import java.net.Socket
import javax.inject.Singleton
import javax.net.SocketFactory

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkModule {

    @Binds
    @Singleton
    fun networkApi(impl: SwitchingNetworkApi): NetworkApi

    @Binds
    @Singleton
    fun networkApiRepository(impl: NetworkApiRepositoryImpl): NetworkApiRepository

    companion object {
        @Provides
        @Singleton
        fun okHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .socketFactory(DelegatingSocketFactory)
                .build()
        }

        private object DelegatingSocketFactory : SocketFactory() {
            private val socketFactory: SocketFactory = getDefault()

            override fun createSocket(): Socket =
                socketFactory.createSocket().setTag()

            override fun createSocket(host: String?, port: Int): Socket =
                socketFactory.createSocket(host, port).setTag()

            override fun createSocket(
                host: String?,
                port: Int,
                localHost: InetAddress?,
                localPort: Int,
            ): Socket =
                socketFactory.createSocket(host, port, localHost, localPort).setTag()

            override fun createSocket(host: InetAddress?, port: Int): Socket =
                socketFactory.createSocket(host, port).setTag()

            override fun createSocket(
                address: InetAddress?,
                port: Int,
                localAddress: InetAddress?,
                localPort: Int,
            ): Socket =
                socketFactory.createSocket(address, port, localAddress, localPort).setTag()

            private fun Socket.setTag(): Socket = apply {
                TrafficStats.setThreadStatsTag(0xF00D)
                TrafficStats.tagSocket(this)
            }
        }
    }
}
