plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hanoicraftcorner"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.hanoicraftcorner"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SMTP_EMAIL", "\"${property("SMTP_EMAIL")}\"")
        buildConfigField("String", "SMTP_APP_PASSWORD", "\"${property("SMTP_APP_PASSWORD")}\"")
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

    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }
}

dependencies {
    implementation ("com.github.bumptech.glide:glide:4.12.0") // Kiểm tra phiên bản mới nhất
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0") // Kiểm tra phiên bản mới nhất
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.litert.support.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.rules)

//    Firebase
    implementation(platform(libs.firebase.bom.v33150))
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.firestore.v24100)

//    JavaMail
    implementation(libs.android.mail)
    implementation(libs.android.activation)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation(libs.okhttp)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
}