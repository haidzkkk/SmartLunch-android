package com.fpoly.smartlunch.di.modules

import android.content.Context
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.data.network.ChatApi
import com.fpoly.smartlunch.data.network.ProductApi
import com.fpoly.smartlunch.data.network.RemoteDataSource
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.data.network.SocketManager
import com.fpoly.smartlunch.data.network.UserApi
import com.fpoly.smartlunch.data.repository.AuthRepository
import com.fpoly.smartlunch.data.repository.ChatRepository
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    fun providerSessionManager(
        context: Context
    ) : SessionManager = SessionManager(context.applicationContext)

    @Provides
    fun providerRemoteDateSource(): RemoteDataSource = RemoteDataSource()

    @Provides
    fun providerApiUser(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): UserApi = remoteDataSource.buildApi(UserApi::class.java, context)

    @Provides
    fun providerApiProduct(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ProductApi = remoteDataSource.buildApi(ProductApi::class.java, context)

    @Provides
    fun providerHomeRepository(
        api: ProductApi
    ): HomeRepository = HomeRepository(api)

    @Provides
    fun providerProductRepository(
        api: ProductApi
    ): ProductRepository = ProductRepository(api)


    @Provides
    fun providerApiAuth(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): AuthApi = remoteDataSource.buildApi(AuthApi::class.java, context)

    @Provides
    fun providerAuthRepository(
        api: AuthApi
    ): AuthRepository = AuthRepository(api)

    @Provides
    fun providerApiChat(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ChatApi = remoteDataSource.buildApi(ChatApi::class.java, context)

    @Provides
    fun providerChatRepository(
        chatApi: ChatApi,
        authApi: AuthApi,
        socketManager: SocketManager
    ): ChatRepository = ChatRepository(chatApi, authApi, socketManager)

    @Provides
    fun providerSocketManger(
    ): SocketManager = SocketManager()
}

