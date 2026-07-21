import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
}

// Carga las claves locales desde local.properties (nunca se versiona; ver .gitignore)
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.labajada.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.labajada.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // La key real vive en local.properties (no versionado). Si falta, falla el build
        // a propósito para que no se te olvide configurarla en cada máquina/CI.
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")
            ?: throw GradleException(
                "Falta MAPS_API_KEY en local.properties. Agrega: MAPS_API_KEY=tu_key_aqui"
            )
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
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
        sourceCompatibility = JavaVersion.VERSION_17 // Actualizado a Java 17, obligatorio para las versiones modernas de Compose y Room
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    //FireBase
    implementation(platform("com.google.firebase:firebase-bom:34.15.0"))
    implementation("com.google.firebase:firebase-analytics")

    // SingInGoogle
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // OBLIGATORIO: Soporte e Implementación de Jetpack Compose (UI Moderna)
    implementation("com.google.firebase:firebase-auth")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Room (BD Local Offline para favoritos e historial con KSP)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Red y Consumo de API (Retrofit con Moshi según tu informe)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)

    // Conrrutinas y Ciclo de vida para Compose
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Mapas de Google y Localización GPS
    implementation(libs.google.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Preferencias Locales y Carga de Fotos
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)

    implementation("androidx.compose.material:material-icons-extended")


    implementation("com.google.firebase:firebase-firestore")
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //fix Edicion De Imagenes dentro de la app
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // Edicion de Imagenes
    implementation("com.github.yalantis:ucrop:2.2.11")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // preview
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // build.gradle (module)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")
}