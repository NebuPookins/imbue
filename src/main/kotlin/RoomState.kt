import com.beust.klaxon.JsonObject
import org.apache.commons.lang3.RandomStringUtils
import org.eclipse.jetty.websocket.api.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class RoomState {
	companion object {
		val log: Logger = LoggerFactory.getLogger(RoomState::class.java)
	}

	private val users = ConcurrentHashMap<Session, UserData>()

	@Synchronized
	fun add(session: Session): UserData {
		val userNamesInUse = users.values.map { it.username }.toSet()
		fun findUnusedName(digits: Int): String {
			val candidateName = "User${RandomStringUtils.randomNumeric(digits)}"
			return if (userNamesInUse.contains(candidateName)) {
				findUnusedName(digits + 1)
			} else {
				candidateName
			}
		}
		val user = UserData(session, findUnusedName(1))
		users[session] = user
		return user
	}

	fun broadcast(message: JsonObject) {
		users.keys.forEach {
			it.remote.sendString(message.toJsonString())
		}
	}

	fun remove(session: Session): UserData? =
		users.remove(session)

	fun get(session: Session): UserData? = users[session]

	fun keepAlive(): Nothing {
		val sleepMillis = 60_000L
		while (true) {
			Thread.sleep(sleepMillis)
			log.info("Keeping alive ${users.size} connections...")
			users.keys.forEach { it.remote.sendString(KeepAlive().toJson().toJsonString())}
		}
	}
}