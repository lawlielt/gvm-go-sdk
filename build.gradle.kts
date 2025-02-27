plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
//    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.lawlielt"
version = "0.1.0-beta.2"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2024.3.3")  // 设置 GoLand 的版本
    type.set("GO")
    plugins.set(listOf(
        "org.jetbrains.plugins.go"  // 指定 Go 插件版本
    ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    // 设置插件的 JAR 打包任务
//    register<Jar>("buildPlugin") {
//        dependsOn("build") // 确保插件构建完毕后打包
//        from("build/distributions")  // 插件的构建目录
//        archiveBaseName.set("goland-gvm-plugin")  // JAR 文件名
//        archiveVersion.set("1.0")  // 设置版本
//    }

    // 构建插件 JAR 文件的任务
    buildPlugin {

    }
}
