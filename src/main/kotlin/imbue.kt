import com.beust.klaxon.Klaxon
import org.eclipse.jetty.websocket.api.StatusCode
import org.eclipse.jetty.websocket.api.annotations.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Spark.*
import kotlin.concurrent.thread
import org.eclipse.jetty.websocket.api.Session as WsSession

data class UserData(val session: WsSession, val username: String)

@WebSocket
@Suppress("unused") //Used via reflection
class WebSocketHandler {
	companion object {
		val log: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
	}

	private val connectedSessions = RoomState()

	@OnWebSocketConnect
	fun onWebSocketConnect(session: WsSession) {
		val user = connectedSessions.add(session)
		log.info("${user.username} joined room")
		session.remote.sendString(SetName(user.username).toJson().toJsonString())
		connectedSessions.broadcast(AddSystemLine("${user.username} joined room").toJson())
	}

	@OnWebSocketClose
	fun onWebSocketClose(session: WsSession, statusCode: Int, reason: String?) {
		val maybeUserData = connectedSessions.remove(session)
		when (maybeUserData) {
			is UserData -> connectedSessions.broadcast(AddSystemLine("${maybeUserData.username} left room.").toJson())
			null -> log.warn("Tried to remove session that was already removed.")
		}
		when (statusCode) {
			StatusCode.SHUTDOWN -> log.info("Session disconnected. Reason: $reason")
			else -> log.warn("Session disconnected. Status Code $statusCode reason $reason")
		}
	}

	@OnWebSocketMessage
	fun onWebSocketText(session: WsSession, rawMessage: String?) {
		val sender = connectedSessions.get(session)
		if (sender == null) {
			log.warn("Could not find userdata for $session")
			session.disconnect()
			return
		}
		if (rawMessage == null) {
			log.warn("$sender sent null message")
			return
		}
		val message = Klaxon().parse<ClientMessage>(rawMessage)
		if (message == null) {
			log.warn("Could not parse message $rawMessage")
			return
		}
		when (message.type) {
			"Message" ->
				connectedSessions.broadcast(AddUserLine(sender.username, message.payload).toJson())
			"KeepAlive" -> {
				//Do nothing
			}
			else ->
				log.warn("Unknown message type $message")
		}
	}

	@OnWebSocketError
	fun onWebSocketError(cause: Throwable?) {
		log.error("WebSocket Error", cause)
	}

	fun keepConnectionsAlive(): Nothing {
		connectedSessions.keepAlive()
	}
}

fun main(args: Array<String>) {
	staticFiles.location("/public")
	val webSocketHandler = WebSocketHandler()
	webSocket("/ws", webSocketHandler)
	port(4568)
	init()
	thread {
		webSocketHandler.keepConnectionsAlive()
	}
}
