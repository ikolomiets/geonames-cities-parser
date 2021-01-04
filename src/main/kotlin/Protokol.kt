import com.electrit.protokol.Protokol
import com.electrit.protokol.ProtokolMapEntry
import com.electrit.protokol.ProtokolObject

class GeoLocation(
    var latitude: Float = 0f,
    var longitude: Float = 0f,
    var population: Int = 0,
    var admin1: String = "",
    var admin2: String = ""
) {
    override fun toString(): String {
        return "GeoLocation(latitude=$latitude, longitude=$longitude, admin1='$admin1', admin2='$admin2')"
    }
}

object GeoLocationProtokolObject : ProtokolObject<GeoLocation> {
    override fun create() = GeoLocation()

    override fun use(value: GeoLocation, p: Protokol) = with(p) {
        with(value) {
            FLOAT(::latitude)
            FLOAT(::longitude)
            STRING(::admin1)
            STRING(::admin2)
        }
    }
}

object GeoCitesProtokolObject : ProtokolObject<ProtokolMapEntry<String, List<GeoLocation>>> {
    override fun create() = ProtokolMapEntry<String, List<GeoLocation>>("", emptyList())

    override fun use(value: ProtokolMapEntry<String, List<GeoLocation>>, p: Protokol) = with(p) {
        with(value) {
            STRING(::key)
            OBJECTS(::value, GeoLocationProtokolObject)
        }
    }
}
