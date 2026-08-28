import java.util.Properties
import java.io.FileInputStream


plugins {

    alias(libs.plugins.android.application)

    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)

    id("org.jetbrains.kotlin.kapt")

}



val localProperties = Properties()

val localPropertiesFile =
    rootProject.file("local.properties")


if (localPropertiesFile.exists()) {

    localProperties.load(
        FileInputStream(localPropertiesFile)
    )

}


val siliconApiKey =
    localProperties.getProperty(
        "SILICON_API_KEY"
    ) ?: ""





android {


    namespace =
        "com.example.aichatapp"


    compileSdk = 35



    defaultConfig {


        applicationId =
            "com.example.aichatapp"


        minSdk = 24


        targetSdk = 35


        versionCode = 1


        versionName = "1.0"



        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"

    }



    buildTypes {


        debug {

            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://10.0.2.2:8000/\""
            )

        }



        release {


            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://api.xxx.com/\""
            )


            isMinifyEnabled = false


        }


    }





    buildFeatures {

        compose = true

        buildConfig = true

    }







    compileOptions {


        sourceCompatibility =
            JavaVersion.VERSION_21


        targetCompatibility =
            JavaVersion.VERSION_21

    }



}




kotlin {

    jvmToolchain(21)

}






dependencies {


        implementation(
            "androidx.datastore:datastore-preferences:1.1.1"
        )



    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )



    implementation(
        platform(
            libs.androidx.compose.bom
        )
    )


    implementation(
        libs.androidx.compose.material3
    )


    implementation(
        libs.androidx.compose.ui
    )


    implementation(
        libs.androidx.compose.ui.graphics
    )


    implementation(
        libs.androidx.compose.ui.tooling.preview
    )



    implementation(
        "androidx.compose.material:material-icons-extended"
    )
    implementation(libs.play.services.cloud.messaging)


    debugImplementation(
        libs.androidx.compose.ui.tooling
    )





    implementation(
        libs.androidx.navigation.compose
    )






    implementation(
        "androidx.room:room-runtime:2.7.0"
    )


    implementation(
        "androidx.room:room-ktx:2.7.0"
    )


    kapt(
        "androidx.room:room-compiler:2.7.0"
    )






    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )


    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )







    implementation(
        libs.hilt.android
    )


    implementation(
        libs.androidx.hilt.navigation.compose
    )


    kapt(
        libs.hilt.compiler
    )





    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )





    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
    )


    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )


    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )





    implementation(
        "androidx.core:core-ktx:1.15.0"
    )


}