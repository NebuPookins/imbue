sealed class ServerMessage

data class SetName(val name: String): ServerMessage()

data class AddSystemLine(val line: String): ServerMessage()

data class AddUserLine(val sender: String, val line: String): ServerMessage()

class KeepAlive: ServerMessage()

data class ClientMessage(val type: String, val payload: String)

