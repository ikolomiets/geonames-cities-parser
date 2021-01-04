import com.electrit.protokol.ByteArrayProtokolCodec
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val geoCitiesProtokolBytes = File("geoCities.protokol").readBytes()
    val millis1 = measureTimeMillis {
        val geoCities = ByteArrayProtokolCodec.decodeMap(geoCitiesProtokolBytes, GeoCitesProtokolObject)
        println(geoCities["CA.Richmond Hill"])
        println(geoCities["US.Springfield"])
    }
    println("geoCities.protokol decode time: $millis1")

    val geoCitiesJsonText = File("geoCities.json").readText()
    val millis2 = measureTimeMillis {
        val map = Json.decodeFromString<Map<String, String>>(geoCitiesJsonText)
        println(map["CA.Richmond Hill"])
        println(map["US.Springfield"])
    }
    println("geoCities.json decode time: $millis2")
}