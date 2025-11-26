plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.unicafe"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        applicationId = "com.example.unicafe"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- LIBRERÍAS DEL CATALOGO (libs.versions.toml) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)

    // --- REDES (Retrofit & OkHttp) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // --- IMÁGENES (Glide) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- VIDEO Y MEDIA (Media3) ---
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-extractor:1.4.1")

    // Viejo ExoPlayer (Solo si es estrictamente necesario, si no, bórralo)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // --- PRUEBAS UNITARIAS ---
    // Quitamos duplicados que ya estaban en 'libs' si existen, dejamos los manuales seguros
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation(kotlin("test"))

    // --- PRUEBAS DE INSTRUMENTACIÓN ---
    // Usamos las versiones del catálogo (libs) para evitar conflictos de versiones duplicadas
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")// --- LIBRERÍAS DEL CATALOGO (libs.versions.toml) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)

    // --- REDES (Retrofit & OkHttp) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // --- IMÁGENES (Glide) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- VIDEO Y MEDIA (Media3) ---
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-extractor:1.4.1")

    // Viejo ExoPlayer (Solo si es estrictamente necesario, si no, bórralo)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // --- PRUEBAS UNITARIAS ---
    // Quitamos duplicados que ya estaban en 'libs' si existen, dejamos los manuales seguros
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation(kotlin("test"))

    // --- PRUEBAS DE INSTRUMENTACIÓN ---
    // Usamos las versiones del catálogo (libs) para evitar conflictos de versiones duplicadas
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    implementation("com.google.android.material:material:1.x.x")

}