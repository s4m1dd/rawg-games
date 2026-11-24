# RAWG Games — Android Technical Assessment (CAN Creative)

Kotlin + XML Views, arsitektur **MVVM**, di luar itu hanya library Jetpack/AndroidX resmi
(tanpa Retrofit, OkHttp, Glide/Coil, Paging3, Hilt, atau Navigation).

## Fitur

- Games list — `GET /api/games`
- Paging — `page` + `page_size=20`, load more saat scroll mendekati akhir
- Search — `?search=`, debounce 350 ms
- Detail — `GET /api/games/{id}`
- Favorite — Room (list, add, delete), bertahan setelah app ditutup

## Arsitektur

```
ui/          Fragment + ViewModel (StateFlow) + RecyclerView adapter
data/        GameRepository — satu-satunya tempat logika remote + lokal
  remote/    RawgClient (HttpURLConnection) + RawgJson (org.json), tanpa library HTTP
  local/     Room: FavoriteGame entity + FavoriteDao
util/        ImageLoader — HttpURLConnection + BitmapFactory + LruCache
```

`ServiceLocator.repository` sengaja bisa di-swap supaya UI test memakai fake backend
(tanpa jaringan, tanpa database asli).

## Setup

1. JDK 17 — `brew install openjdk@17`, lalu
   `export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home`
2. Android SDK — `local.properties` berisi `sdk.dir=/Users/<user>/Library/Android/sdk`
3. API key — daftar di https://rawg.io/apidocs, lalu tambahkan ke `local.properties`:

   ```
   RAWG_API_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ```

   Tanpa key, app menampilkan pesan dan tidak melakukan request.

## Build & test

```bash
./gradlew assembleDebug        # build APK
./gradlew test                 # unit test (8 test)
adb devices                    # pastikan HP terpasang
./gradlew connectedDebugAndroidTest   # UI test (Espresso)
```

Unit test mencakup parsing JSON (termasuk `next: null`), URL builder, dan toggle favorite
dengan fake DAO. UI test menjalankan alur: list → detail → add favorite → tab Favorites → delete.
