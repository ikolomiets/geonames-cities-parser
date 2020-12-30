import com.electrit.protokol.ByteArrayProtokolCodec
import com.electrit.protokol.Protokol
import com.electrit.protokol.ProtokolObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.measureTimeMillis

data class GeoLocation(
    var latitudeBits: Int = 0,
    var longitudeBits: Int = 0,
    var population: Int = 0,
    var admin1: String = "",
    var admin2: String = ""
) {
    var latitude
        get() = Float.fromBits(latitudeBits)
        set(latitude) {
            latitudeBits = latitude.toBits()
        }

    var longitude
        get() = Float.fromBits(longitudeBits)
        set(longitude) {
            longitudeBits = longitude.toBits()
        }

    override fun toString(): String {
        return "GeoLocation(latitudeBits=$latitudeBits, longitudeBits=$longitudeBits, admin1='$admin1', admin2='$admin2', latitude=$latitude, longitude=$longitude, population=$population)"
    }

}

data class GeoCity(
    var countryCity: String = "",
    var locations: List<GeoLocation> = emptyList()
)

data class Geonames(
    var geoCities: List<GeoCity> = emptyList()
)

object GeoLocationProtokolObject : ProtokolObject<GeoLocation> {
    override fun create() = GeoLocation()

    override fun use(value: GeoLocation, p: Protokol) = with(p) {
        with(value) {
            INT(::latitudeBits)
            INT(::longitudeBits)
            STRING(::admin1)
            STRING(::admin2)
        }
    }
}

object GeoCityProtokolObject : ProtokolObject<GeoCity> {
    override fun create() = GeoCity()

    override fun use(value: GeoCity, p: Protokol) = with(p) {
        with(value) {
            STRING(::countryCity)
            OBJECTS(::locations, GeoLocationProtokolObject)
        }
    }
}

object GeonamesProtokolObject : ProtokolObject<Geonames> {
    override fun create() = Geonames()

    override fun use(value: Geonames, p: Protokol) = with(p) {
        with(value) {
            OBJECTS(::geoCities, GeoCityProtokolObject)
        }
    }
}

fun createLocation(lat: String, lon: String): GeoLocation {
    val location = GeoLocation()
    location.latitude = lat.toFloat()
    location.longitude = lon.toFloat()
    return location
}

fun main() {
    val protokolBytes = File("geonames.protokol").readBytes()
    println("geonames.protokol size ${protokolBytes.size}")

    var geonames: Geonames?

    val millis = measureTimeMillis {
        geonames = ByteArrayProtokolCodec.decode(protokolBytes, GeonamesProtokolObject)
    }

    println("decode time: $millis")
    
    for (geoCity in geonames!!.geoCities) {
        if (geoCity.countryCity == "CA.Richmond Hill") {
            println(geoCity)
            break
        }
    }

    val geoCitiesJsonText = File("geoCities.json").readText()
    println("geoCitiesJsonText size: ${geoCitiesJsonText.toByteArray().size}")

    val millis1 = measureTimeMillis {
        val map = Json.decodeFromString<Map<String, String>>(geoCitiesJsonText)
        println(map["CA.Richmond Hill"])
        println(map["US.Springfield"])
    }

    println("json decode time: $millis1")
}