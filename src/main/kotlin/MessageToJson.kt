import com.beust.klaxon.JsonObject
import com.beust.klaxon.json

fun ServerMessage.toJson(): JsonObject {
	val self = this
	return when (self) {
		is SetName -> json {
			obj(
					"type" to "SetName",
					"name" to self.name
			)
		}
		is AddSystemLine -> json {
			obj(
					"type" to "AddSystemLine",
					"line" to self.line
			)
		}
		is AddUserLine -> json {
			obj(
					"type" to "AddUserLine",
					"from" to self.sender,
					"line" to self.line
			)
		}
		is KeepAlive -> json {
			obj(
					"type" to "KeepAlive"
			)
		}
	}
}