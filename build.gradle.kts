import com.aliucord.gradle.AliucordExtension
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/snapshots")
        maven("https://jitpack.io")
    }

    dependencies {
        // 稍微升級 Gradle 插件以支援新版 Kotlin
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("com.aliucord:gradle:main-SNAPSHOT")
        // 【關鍵修改】這裡升級到了 1.9.21，解決你的報錯
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.aliucord.com/snapshots")
    }
}

fun Project.aliucord(configuration: AliucordExtension.() -> Unit) = extensions.getByName<AliucordExtension>("aliucord").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "com.aliucord.gradle")
    apply(plugin = "kotlin-android")

    aliucord {
        // 你可以之後再改這裡的名字，目前先保持這樣能跑通就行
        author("RazerTexZ", 123456789L)
        updateUrl.set("https://raw.githubusercontent.com/USERNAME/REPONAME/builds/updater.json")
        buildUrl.set("https://raw.githubusercontent.com/USERNAME/REPONAME/builds/%s.zip")
    }

    android {
        // 配合新版 Android SDK
        compileSdkVersion(33) 

        defaultConfig {
            minSdk = 24
            targetSdk = 33
        }

        compileOptions {
            // 【關鍵修改】升級到 Java 17
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                // 【關鍵修改】升級到 Java 17
                jvmTarget = "17" 
                freeCompilerArgs = freeCompilerArgs +
                        "-Xno-call-assertions" +
                        "-Xno-param-assertions" +
                        "-Xno-receiver-assertions"
            }
        }
    }

    dependencies {
        val discord by configurations
        val implementation by configurations

        discord("com.discord:discord:aliucord-SNAPSHOT")
        implementation("com.aliucord:Aliucord:main-SNAPSHOT")

        implementation("androidx.appcompat:appcompat:1.4.0")
        implementation("com.google.android.material:material:1.4.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.2")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
