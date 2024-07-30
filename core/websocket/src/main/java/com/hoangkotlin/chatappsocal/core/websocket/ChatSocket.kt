//package com.hoangkotlin.chatappsocial.core.websocket
//
//import com.hoangkotlin.chatappsocial.core.network.retrofit.NetworkConfig
//import com.hoangkotlin.chatappsocial.core.common.Dispatcher
//import com.hoangkotlin.chatappsocial.core.common.SocialDispatchers.IO
//import com.hoangkotlin.chatappsocial.core.datastore.SocialPreferencesDataSource
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.onEach
//import kotlinx.coroutines.launch
//import kotlinx.serialization.json.JsonObject
//import org.hildan.krossbow.stomp.StompClient
//import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
//import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
//import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
//import org.hildan.krossbow.stomp.use
//import javax.inject.Inject
//import kotlin.coroutines.CoroutineContext
//
//// Should be only responsible for socket state
//// connect, disconnect, reconnect,...
//class ChatSocket @Inject constructor(
//    private val client: StompClient,
//    localDataSource: SocialPreferencesDataSource,
//    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
//) : CoroutineScope {
//    private val job: Job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = ioDispatcher + job
//    private val appData = localDataSource.appUserData
//
//    var connectionState: String = "Idle"
//    val messages = Channel<JsonObject>(Channel.CONFLATED)
//
//    private var stompSession: StompSessionWithKxSerialization? = null
//
//    fun connect() {
//        launch {
//            stompSession = client.connect(NetworkConfig.WS_URL)
//                .withJsonConversions()
//        }
//        launch {
//            stompSession?.use { session ->
//                val messages: Flow<JsonObject> = session.subscribe(
//                    "/topic/messages/" +
//                            "${appData.first().currentUsername}", JsonObject.serializer()
//                ).onEach {
//                    messages.trySend(it)
//                }
//            }
//        }
//
//    }
//
//
//
//
////    internal sealed class State {
////        data object Connecting : State()
////
////        data class Connected(val event: ConnectedEvent) : State()
////        data object NetworkDisconnected : State()
////
////        data class DisconnectedTemporarily(val error: ChatNetworkError?) : State()
////        data class DisconnectedPermanently(val error: ChatNetworkError?) : State()
////        data object DisconnectedByRequest : State()
////    }
//}