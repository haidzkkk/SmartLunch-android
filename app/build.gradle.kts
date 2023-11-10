plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.fpoly.smartlunch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fpoly.smartlunch"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures.viewBinding = true
}

kapt {
    correctErrorTypes = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.paging:paging-runtime-ktx:3.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // extention
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // navigation component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.2")

    // bottom nav
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    // glide image
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // mvrx
    implementation("com.airbnb.android:mvrx:1.5.1")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    //okhttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.9.1")

    // rxjava rxandroid
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    implementation("com.jakewharton.rxrelay2:rxrelay:2.1.1")

    // dagger
    implementation("com.google.dagger:dagger:2.48")
    implementation("com.google.dagger:dagger-android-support:2.48")

    kapt("com.google.dagger:dagger-compiler:2.48")?.let { implementation(it) }
//    kapt("com.google.dagger:dagger-android-processor:2.35.1")?.let { implementation(it) }
//    kapt("androidx.room:room-compiler:2.5.1")?.let { implementation(it) }

    // Effects imageview
    implementation("com.airbnb.android:lottie:6.0.1")

    // show image
    implementation("com.github.stfalcon-studio:StfalconImageViewer:v1.0.1")

    // map
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.google.firebase:firebase-messaging-ktx")

    // Socket
    implementation ("io.socket:socket.io-client:2.0.0")

    // gson
    implementation ("com.google.code.gson:gson:2.10.1")

    // webrtc
    implementation ("com.mesibo.api:webrtc:1.0.5")


    // request permission
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

    // circle image view
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // image slider

    // swipe load
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // paypal sdk
    implementation("com.paypal.checkout:android-sdk:0.112.2")
}
