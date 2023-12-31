package com.fpoly.smartlunch.di.modules

import android.app.Application
import android.content.Context
import com.fpoly.smartlunch.data.network.AuthApi
import com.fpoly.smartlunch.data.network.ChatApi
import com.fpoly.smartlunch.data.network.CommentApi
import com.fpoly.smartlunch.data.network.ContentDataSource
import com.fpoly.smartlunch.data.network.NotificationApi
import com.fpoly.smartlunch.data.network.OrderApi
import com.fpoly.smartlunch.data.network.PlacesApi
import com.fpoly.smartlunch.data.network.ProductApi
import com.fpoly.smartlunch.data.network.ProvinceAddressApi
import com.fpoly.smartlunch.data.network.RemoteDataSource
import com.fpoly.smartlunch.data.network.SessionManager
import com.fpoly.smartlunch.data.network.SocketManager
import com.fpoly.smartlunch.data.network.UserApi
import com.fpoly.smartlunch.data.network.ZalopayApi
import com.fpoly.smartlunch.data.repository.AuthRepository
import com.fpoly.smartlunch.data.repository.ChatRepository
import com.fpoly.smartlunch.data.repository.HomeRepository
import com.fpoly.smartlunch.data.repository.NotificationRepository
import com.fpoly.smartlunch.data.repository.PaymentRepository
import com.fpoly.smartlunch.data.repository.PlacesRepository
import com.fpoly.smartlunch.data.repository.ProductRepository
import com.fpoly.smartlunch.ui.call.call.WebRTCClient
import com.fpoly.smartlunch.data.repository.UserRepository
import com.google.rpc.context.AttributeContext.Auth
import dagger.Module
import dagger.Provides

@Module
object NetworkModule {

    @Provides
    fun providerWebRTCClient(
        context: Context,
    ) : WebRTCClient = WebRTCClient(context as Application)

    @Provides
    fun providerSocketManger(
    ): SocketManager = SocketManager()

    @Provides
    fun providerSessionManager(
        context: Context
    ) : SessionManager = SessionManager(context.applicationContext)
    @Provides
    fun providerContentDataSource(
        context: Context
    ) : ContentDataSource = ContentDataSource(context.contentResolver)

    @Provides
    fun providerRemoteDateSource(): RemoteDataSource = RemoteDataSource()

    @Provides
    fun providerHomeRepository(
        userApi: AuthApi,
        productApi: ProductApi,
        contentDataSource: ContentDataSource
    ): HomeRepository = HomeRepository(userApi, productApi, contentDataSource)

    @Provides
    fun providerApiPlaces(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): PlacesApi = remoteDataSource.buildApiOpenStreetMap(PlacesApi::class.java, context)

    @Provides
    fun providerPlacesRepository(
        api: PlacesApi
    ): PlacesRepository = PlacesRepository(api)

    @Provides
    fun providerApiNotification(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): NotificationApi = remoteDataSource.buildApi(NotificationApi::class.java, context)

    @Provides
    fun providerNotificationRepository(
        api: NotificationApi
    ): NotificationRepository = NotificationRepository(api)

    @Provides
    fun providerApiUser(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): UserApi = remoteDataSource.buildApi(UserApi::class.java, context)

    @Provides
    fun providerUserRepository(
        api: UserApi,
        provinceAddressApi: ProvinceAddressApi,
    ): UserRepository = UserRepository(api, provinceAddressApi)

    @Provides
    fun providerApiProduct(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ProductApi = remoteDataSource.buildApi(ProductApi::class.java, context)

    @Provides
    fun providerProductRepository(
        api: ProductApi,
        commentApi: CommentApi
    ): ProductRepository = ProductRepository(api, commentApi)

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
        socketManager: SocketManager,
        contentDataSource: ContentDataSource
    ): ChatRepository = ChatRepository(chatApi, authApi, socketManager, contentDataSource)

    @Provides
    fun providerApiOrder(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): OrderApi = remoteDataSource.buildApi(OrderApi::class.java, context)
    @Provides
    fun providerApiProvince(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ProvinceAddressApi = remoteDataSource.buildApiProvince(ProvinceAddressApi::class.java, context)

    @Provides
    fun providerPaymentRepository(
        OrderApi: OrderApi,
        zalopayApi: ZalopayApi,
    ): PaymentRepository = PaymentRepository(OrderApi, zalopayApi)

    @Provides
    fun providerApiComment(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): CommentApi = remoteDataSource.buildApi(CommentApi::class.java, context)

    @Provides
    fun providerApiZalopay(
        remoteDataSource: RemoteDataSource,
        context: Context
    ): ZalopayApi = remoteDataSource.buildApiOrderZalopay(ZalopayApi::class.java, context)
}

