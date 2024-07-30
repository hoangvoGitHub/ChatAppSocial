package com.hoangkotlin.chatappsocial.core.network.di

import com.hoangkotlin.chatappsocial.core.network.api.AuthenticationApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatChannelApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatFileApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatFriendApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatMessageApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatUserApi
import com.hoangkotlin.chatappsocial.core.network.api.DeviceApi
import com.hoangkotlin.chatappsocial.core.network.api.SearchApi
import com.hoangkotlin.chatappsocial.core.network.downloader.Downloader
import com.hoangkotlin.chatappsocial.core.network.downloader.SocialDownloader
import com.hoangkotlin.chatappsocial.core.network.retrofit.NetworkConfig
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressInterceptor
import com.hoangkotlin.chatappsocial.core.network.utils.ServiceInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        // also decode default values in data class
        encodeDefaults = true
    }

    @Provides
    @Singleton
    @Named(RestClient)
    fun providesRestClient(
        serviceInterceptor: ServiceInterceptor,
        progressInterceptor: ProgressInterceptor,
    ) = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            },
        )
        .addInterceptor(serviceInterceptor)
        .addInterceptor(progressInterceptor)
        .build()

    @Provides
    @Singleton
    @Named(WSClient)
    fun providesWsClient(
        serviceInterceptor: ServiceInterceptor
    ) = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            },
        ).addInterceptor(serviceInterceptor)

        .hostnameVerifier { _, _ -> true }.build()


    @Provides
    fun providesAuthService(
        retrofit: Retrofit
    ): AuthenticationApi = retrofit.create()

    @Provides
    fun providesChannelApi(
        retrofit: Retrofit
    ): ChatChannelApi = retrofit.create()

    @Provides
    fun providesMessageApi(
        retrofit: Retrofit
    ): ChatMessageApi = retrofit.create()

    @Provides
    fun providesUserApi(
        retrofit: Retrofit
    ): ChatUserApi = retrofit.create<ChatUserApi>()

    @Provides
    fun providesSearchApi(
        retrofit: Retrofit
    ): SearchApi = retrofit.create()

    @Provides
    fun providesFriendApi(
        retrofit: Retrofit
    ): ChatFriendApi = retrofit.create()

    @Provides
    fun providesDeviceApi(
        retrofit: Retrofit
    ): DeviceApi = retrofit.create()

    @Provides
    fun providesFileApi(
        retrofit: Retrofit
    ): ChatFileApi = retrofit.create()

    @Provides
    @Singleton
    fun providesRetrofit(
        @Named(RestClient) okhttpCallFactory: OkHttpClient,
        networkJson: Json
    ): Retrofit = Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .client(okhttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType()),
        ).build()


    const val RestClient = "rest_client"
    const val WSClient = "ws_client"

    @Module
    @InstallIn(SingletonComponent::class)
    interface NetworkBindModule {
        @Binds
        fun bindsDownloader(
            downloader: SocialDownloader
        ): Downloader
    }

}