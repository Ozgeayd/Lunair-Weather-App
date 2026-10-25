# 🌙 Lunair — Modern Weather Application

Lunair, modern Android geliştirme standartları, **Jetpack Compose** ve **Clean Architecture** prensipleri benimsenerek geliştirilmiş; akıcı geçişlere, konum tabanlı tahminlere ve bağlamsal günlük yaşam önerilerine sahip şık bir hava durumu uygulamasıdır.

---

## ✨ Öne Çıkan Özellikler

* **📍 Gerçek Zamanlı GPS & Canlı Konum Kartı:** Kullanıcının anlık konumunu otomatik tespit ederek o konuma ait sıcaklık ve hava durumunu dinamik degrade zeminle sunar.
* **🔄 Akıcı Şehir Geçişi (`HorizontalPager`):** Şehirler arasında yatay kaydırma ile kesintisiz geçiş ve dinamik sayfalama göstergesi (Pager Indicator).
* **🗑️ Kolay Favori Yönetimi (`Swipe-to-Dismiss` & Undo):** Favori şehirleri sola kaydırarak listeden çıkarma ve yanlışlıkla silmelere karşı `Snackbar` ile anında geri alma desteği.
* **💡 Akıllı Günün Önerisi (Smart Lifestyle & Wardrobe Assistant):** Sıradan hava durumlarının ötesinde; anlık sıcaklık, rüzgâr hızı, yağış ihtimali ve UV indeksini analiz ederek kıyafet tavsiyeleri (ör. *Şemsiye, Rüzgarlık, Güneş Gözlüğü*) ve 10 üzerinden açık hava elverişlilik skoru sunar.
* **⏱️ Dinamik Saatlik & 7 Günlük Tahmin:** İçinde bulunulan saatten başlayan 24 saatlik yatay tahmin şeridi ve sıcaklık aralıklarını görselleştiren haftalık tahmin çubukları.

---

## 🛠️ Mimari & Teknolojiler

Lunair, sürdürülebilirlik, test edilebilirlik ve yüksek performans gözetilerek inşa edilmiştir:

* **UI & Arayüz:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3) — Deklaratif ve reaktif UI mimarisi.
* **Mimari Desen:** MVVM (Model-View-ViewModel) + Clean Architecture (Repository Pattern & Data Mappers).
* **Asenkron & Reaktif Veri:** Kotlin Coroutines & `StateFlow` — `async`/`awaitAll` ile optimize edilmiş paralel API istekleri.
* **API / Uzak Veri:** [Open-Meteo API](https://open-meteo.com/) — Ücretsiz, hızlı ve API anahtarı gerektirmeyen küresel hava verisi entegrasyonu.
* **Ağ & Serileştirme:** Retrofit / OkHttp & Gson.
* **Yerel Depolama:** SharedPreferences tabanlı favori şehir yönetimi.
* **Konum Servisleri:** Google Play Services Location (FusedLocationProviderClient).

---

## 🏛️ Mimari Katman Yapısı

```text
com.example.hava/
├── data/
│   ├── model/         # API yanıtları ve saf veri modelleri (City, WeatherResponse)
│   ├── local/         # Yerel veri yönetimi (FavoritesManager)
│   ├── remote/        # API servis tanımları ve LocationHelper
│   └── repository/    # WeatherRepository (Interface) & WeatherRepositoryImpl
├── ui/
│   ├── components/    # Yeniden kullanılabilir Compose bileşenleri (WeatherDetailSheet, CityCard vb.)
│   ├── model/         # UI'a özel optimize edilmiş mapper modelleri (DetailUiModel)
│   ├── viewmodel/     # Durum yönetimini üstlenen CityViewModel
│   ├── WeatherScreen.kt
│   └── WeatherUtils.kt
