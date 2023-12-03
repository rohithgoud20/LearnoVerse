plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.learnoverse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.learnoverse"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.4")
    implementation("androidx.navigation:navigation-ui:2.7.4")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.stripe:stripe-java:24.0.0")
    implementation("com.stripe:stripe-android:20.35.0")
    implementation("com.android.volley:volley:1.1.0")
    testImplementation("junit:junit:4.13.2")
    implementation ("mysql:mysql-connector-java:5.1.49")
   // implementation("com.wdullaer:materialdatetimepicker:4.3.1")
// Use the appropriate version



//    implementation ("mysql:mysql-connector-java:5.1.49")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    implementation("de.mkammerer:jargon2-api:1.1")
//    implementation("com.kosprov.jargon2:jargon2-core:2.0.0") // Use the latest version

}