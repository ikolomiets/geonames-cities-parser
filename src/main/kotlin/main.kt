
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val geonameDetails = mutableMapOf<Int, String>()
    val cityGeonames = mutableMapOf<String, MutableList<Int>>()
    val citiesMap = mutableMapOf<String, String>()

    fun getGeoDetails(geonameId: Int): List<String> {
        val details = geonameDetails[geonameId]
        return details!!.split("\t")
    }

    val admin1Codes = mutableMapOf<String, String>()

    File("admin1CodesASCII.txt").forEachLine {
        val strings = it.split('\t')
        admin1Codes[strings[0]] = strings[1]
    }

    val admin2Codes = mutableMapOf<String, String>()
    File("admin2Codes.txt").forEachLine {
        val strings = it.split('\t')
        admin2Codes[strings[0]] = strings[1]
    }

    File("cities5000.txt").forEachLine {
        val geonameId = it.substringBefore("\t").toInt()

        geonameDetails[geonameId] = it

        val geoDetails = getGeoDetails(geonameId)

        // cc.name
        val key = geoDetails[8] + "." + geoDetails[1]

        cityGeonames.getOrPut(key, { mutableListOf() }).add(geonameId)
    }

    cityGeonames.forEach {
        if (it.value.size > 1) {
            val latLon = mutableListOf<String>()
            val admin1Suffixes = mutableListOf<String>()
            val admin2Suffixes = mutableListOf<String>()
            for (geonameId in it.value) {
                val details = getGeoDetails(geonameId)

                val lat = details[4]
                val lon = details[5]
                latLon.add("$lat:$lon")

                val cc = details[8]
                val admin1 = details[10]
                val admin2 = details[11]

                val admin1Text = admin1Codes.getOrDefault("$cc.$admin1", admin1)
                admin1Suffixes.add(admin1Text)

                val admin2Text = admin2Codes.getOrDefault("$cc.$admin1.$admin2", admin2)
                admin2Suffixes.add("$admin1Text:$admin2Text")
            }

            val distinctAdmin1 = admin1Suffixes.distinct().size
            val suffixes = when {
                distinctAdmin1 < admin2Suffixes.distinct().size -> admin2Suffixes
                distinctAdmin1 == 1 -> admin1Suffixes.map { "" }
                else -> admin1Suffixes
            }

            citiesMap[it.key] = latLon.zip(suffixes).joinToString(",") { pair ->
                pair.first + (if (pair.second.isNotEmpty()) ":" + pair.second else "")
            }
        } else {
            val details = getGeoDetails(it.value[0])
            val lat = details[4]
            val lon = details[5]
            citiesMap[it.key] = "$lat:$lon"
        }
    }

/*
    println(citiesMap.size)
    println(citiesMap["CA.Concord"])
    println(citiesMap["US.Springfield"])
    println(citiesMap["CA.Vernon"])
    println(citiesMap["RU.Vidyayevo"])
    println(citiesMap["CO.Buenaventura"])
*/

    File("geoCities.json").writeText(Json.encodeToString(citiesMap))

    //println(Json.encodeToString(citiesMap))

}

/*

http://download.geonames.org/export/dump/

The main 'geoname' table has the following fields :
---------------------------------------------------
 0: geonameid         : integer id of record in geonames database
 1: name              : name of geographical point (utf8) varchar(200)
 2: asciiname         : name of geographical point in plain ascii characters, varchar(200)
 3: alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
 4: latitude          : latitude in decimal degrees (wgs84)
 5: longitude         : longitude in decimal degrees (wgs84)
 6: feature class     : see http://www.geonames.org/export/codes.html, char(1)
 7: feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
 8: country code      : ISO-3166 2-letter country code, 2 characters
 9: cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
10: admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
11: admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80)
12: admin3 code       : code for third level administrative division, varchar(20)
13: admin4 code       : code for fourth level administrative division, varchar(20)
14: population        : bigint (8 byte int)
15: elevation         : in meters, integer
16: dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
17: timezone          : the iana timezone id (see file timeZone.txt) varchar(40)
18: modification date : date of last modification in yyyy-MM-dd format

 */