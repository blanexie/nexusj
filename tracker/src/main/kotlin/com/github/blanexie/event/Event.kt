import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 *
 * @FileName:
 *          com.safframework.eventbus.EventData
 * @author: Tony Shen
 * @date: 2019-08-25 00:20
 * @version: V1.0 <描述当前版本功能>
 */

data class EventData<T>(
    val coroutineScope: CoroutineScope,
    val eventDispatcher: CoroutineDispatcher,
    val onEvent: (T) -> Unit,
    val exception: ((Throwable) -> Unit)? = null
) {

    private val channel = Channel<T>()

    init {
        coroutineScope.launch {
            channel.consumeEach { // 消费者循环地消费消息
                launch(eventDispatcher) {
                    try {
                        onEvent(it)
                    } catch (e: Exception) {
                        exception?.invoke(e)
                    }
                }
            }
        }
    }

    fun postEvent(event: Any) {
        if (!channel.isClosedForSend) {
            coroutineScope.launch {
                channel.send(event as T)
            }
        } else {
            println("Channel is closed for send")
        }
    }

    fun cancel() {
        channel.cancel()
    }
}