package com.example.hava.ui

import java.text.SimpleDateFormat
import java.util.*

fun parseWeatherCode(code: Int): Pair<String, String> {
    return when (code) {
        0 -> "Açık" to "☀️"
        1, 2 -> "Parçalı bulutlu" to "⛅"
        3 -> "Çok bulutlu" to "☁️"
        45, 48 -> "Sisli" to "🌫️"
        51, 53, 55 -> "Çiseleyen yağmur" to "🌦️"
        61, 63 -> "Hafif yağmur" to "🌧️"
        65 -> "Kuvvetli yağmur" to "🌧️"
        71, 73, 75 -> "Karlı" to "❄️"
        80, 81, 82 -> "Sağanak yağış" to "⛈️"
        95, 96, 99 -> "Fırtına" to "🌩️"
        else -> "Parçalı bulutlu" to "⛅"
    }
}

fun formatDetailDate(): String {
    val sdf = SimpleDateFormat("d MMMM yyyy · EEEE", Locale("tr"))
    return sdf.format(Date())
}

fun formatDayName(dateString: String, index: Int): String {
    if (index == 0) return "Bugün"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString)
        val formatter = SimpleDateFormat("EEEE", Locale("tr"))
        formatter.format(date ?: Date())
    } catch (_: Exception) {
        dateString
    }
}

fun extractHour(timeString: String): String {
    return try {
        // "2026-10-25T14:00" -> "14:00"
        timeString.substringAfter("T")
    } catch (_: Exception) {
        timeString
    }
}
// API'nin 168 saatlik listesinde şu anki saatin indeksini bulur
fun findCurrentHourIndex(timeList: List<String>?): Int {
    if (timeList.isNullOrEmpty()) return 0
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.getDefault())
    val currentFormatted = sdf.format(Date()) // Örn: 2026-10-25T16

    val index = timeList.indexOfFirst { it.startsWith(currentFormatted) }
    return if (index != -1) index else 0
}

data class DayAdvice(
    val title: String,
    val description: String,
    val itemsToTake: List<String>, // Kıyafet/eşya tavsiyeleri
    val outdoorScore: Int          // 1-10 arası açık hava uygunluğu
)

fun generateDayAdvice(weatherCode: Int, temp: Int, wind: Int, precipitation: Int, uv: Int): DayAdvice {
    val items = mutableListOf<String>()

    // Kıyafet ve eşya kontrolleri
    if (precipitation > 40 || weatherCode in listOf(51, 53, 55, 61, 63, 65, 80, 81, 82)) {
        items.add("☔ Şemsiye")
        items.add("🧥 Su Geçirmez Mont")
    } else if (temp < 10) {
        items.add("🧤 Kalın Mont")
        items.add("🧣 Atkı / Bere")
    } else if (temp in 10..17) {
        items.add("🧥 Hafif Hırka / Ceket")
    } else if (temp in 18..25) {
        items.add("👕 Tişört & Kot")
    } else {
        items.add("🩳 İnce Pamuklu Kıyafetler")
    }

    if (uv >= 5) {
        items.add("🕶️ Güneş Gözlüğü")
        items.add("🧴 Güneş Kremi")
    }

    if (wind > 20) {
        items.add("💨 Rüzgarlık")
    }

    // Başlık, açıklama ve dışarı çıkma skoru
    return when {
        weatherCode in listOf(95, 96, 99) -> DayAdvice(
            title = "Şiddetli Fırtına Uyarısı",
            description = "Gök gürültülü ve rüzgarlı hava etkili olacak. Zorunlu olmadıkça açık alanlardan uzak durun.",
            itemsToTake = items,
            outdoorScore = 2
        )
        precipitation > 50 || weatherCode in listOf(61, 63, 65, 80, 81, 82) -> DayAdvice(
            title = "Yağmurlu ve Islak Bir Gün",
            description = "Aralıklı sağanak geçişleri bekleniyor. Ayaklarınızı kuru tutacak ayakkabılar tercih edin.",
            itemsToTake = items,
            outdoorScore = 4
        )
        weatherCode in listOf(71, 73, 75, 85, 86) -> DayAdvice(
            title = "Karlı ve Soğuk Hava",
            description = "Zeminlerde don ve kayma riski var. Dışarı çıkarken kat kat giyinmeyi unutmayın.",
            itemsToTake = items,
            outdoorScore = 3
        )
        temp in 18..25 && precipitation < 20 && wind < 18 -> DayAdvice(
            title = "Açık Hava İçin Kusursuz",
            description = "Sıcaklık ve esinti dışarıda yürüyüş yapmak, koşuya çıkmak veya kahve içmek için ideal seviyede.",
            itemsToTake = items,
            outdoorScore = 9
        )
        temp < 10 -> DayAdvice(
            title = "Oldukça Serin & Ayaz",
            description = "Rüzgâr hissedilen sıcaklığı daha da düşürebilir. Boğazınızı ve ellerinizi sıcak tutun.",
            itemsToTake = items,
            outdoorScore = 5
        )
        else -> DayAdvice(
            title = "Dengeli Hava Koşulları",
            description = "Gün boyunca ani bir değişim beklenmiyor. Akşam saatlerinde serinlemeye karşı hazırlıklı olun.",
            itemsToTake = items,
            outdoorScore = 7
        )
    }
}
