@file:Suppress("UNCHECKED_CAST")

import groovy.lang.Closure
import io.github.fvarrui.javapackager.gradle.PackagePluginExtension
import io.github.fvarrui.javapackager.gradle.PackageTask
import io.github.fvarrui.javapackager.model.*
import io.github.fvarrui.javapackager.model.Platform
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.7.20-RC"
}

buildscript {
    repositories {
        dependencies {
            classpath("io.github.fvarrui:javapackager:1.6.7")
        }
    }
}

plugins.apply("io.github.fvarrui.javapackager.plugin")

val fatJar = false
val applicationName: String = "RedisFront"
val organization: String = "dromara.org"
val supportUrl: String = "https://dromara.org"


val flatlafVersion = "3.0"
val hutoolVersion = "5.8.10"
val fifesoftVersion = "3.2.0"
val derbyVersion = "10.15.2.0"
val lettuceVersion = "6.2.0.RELEASE"
val logbackVersion = "1.4.1"


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation("io.lettuce:lettuce-core:${lettuceVersion}")
    implementation("io.netty:netty-common:4.1.82.Final")
    implementation("com.formdev:flatlaf:${flatlafVersion}")
    implementation("org.jfree:jfreechart:1.5.3")
    implementation("com.formdev:flatlaf-swingx:${flatlafVersion}")
    implementation("com.formdev:flatlaf-intellij-themes:${flatlafVersion}")
    implementation("com.formdev:flatlaf-extras:${flatlafVersion}")
    implementation("cn.hutool:hutool-extra:${hutoolVersion}")
    implementation("cn.hutool:hutool-json:${hutoolVersion}")
    implementation("cn.hutool:hutool-http:${hutoolVersion}")
    implementation("org.apache.derby:derby:${derbyVersion}")
    implementation("com.fifesoft:rsyntaxtextarea:${fifesoftVersion}")
    implementation("com.fifesoft:rstaui:${fifesoftVersion}")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    implementation("at.swimmesberger:swingx-core:1.6.8")
    implementation("com.jgoodies:jgoodies-forms:1.9.0")
    implementation("commons-net:commons-net:3.8.0")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.intellij:forms_rt:7.0.3")
    implementation("com.jcraft:jsch:0.1.55")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
    testLogging.exceptionFormat = TestExceptionFormat.FULL
}

tasks.jar {

    manifest {
        attributes("Main-Class" to "com.redisfront.RedisFrontApplication")
    }

    exclude("module-info.class")
    exclude("META-INF/versions/*/module-info.class")
    exclude("META-INF/*.SF")
    exclude("META-INF/*.DSA")
    exclude("META-INF/*.LIST")
    exclude("META-INF/*.factories")

    if (fatJar) {
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map {
                    zipTree(it).matching {
                        exclude("META-INF/LICENSE")
                    }
                }
        })
    }
}

configure<PackagePluginExtension> {
    mainClass("com.redisfront.RedisFrontApplication")
    packagingJdk(File(System.getProperty("java.home")))
    bundleJre(true)
    customizedJre(true)
    modules(
        listOf(
            "java.desktop",
            "java.prefs",
            "java.base",
            "java.logging",
            "java.sql",
            "java.naming"
        )
    )
    jreDirectoryName("runtimes")
}

tasks.register<PackageTask>("packageForWindows") {

    val innoSetupLanguageMap = LinkedHashMap<String, String>()
    innoSetupLanguageMap["Chinese"] = "compiler:Languages\\ChineseSimplified.isl"
    innoSetupLanguageMap["English"] = "compiler:Default.isl"

    description = "package For Windows"

    organizationName = organization
    organizationUrl = supportUrl

    platform = Platform.windows
    isCreateZipball = false
    winConfig(closureOf<WindowsConfig> {
        icoFile = getIconFile("RedisFront.ico")
        headerType = HeaderType.gui
        originalFilename = applicationName
        copyright = applicationName
        productName = applicationName
        productVersion = version
        fileVersion = version
        isGenerateSetup = true
        setupLanguages = innoSetupLanguageMap
        isCreateZipball = true
        isGenerateMsi = false
        isGenerateMsm = false
        msiUpgradeCode = version
        isDisableDirPage = false
        isDisableFinishedPage = false
        isDisableWelcomePage = false
    } as Closure<WindowsConfig>)
    dependsOn(tasks.build)
}

tasks.register<PackageTask>("packageForLinux") {
    description = "package For Linux"
    platform = Platform.linux

    organizationName = organization
    organizationUrl = supportUrl

    linuxConfig(
        closureOf<LinuxConfig> {
            pngFile = getIconFile("RedisFront.png")
            isGenerateDeb = true
            isGenerateRpm = true
            isCreateTarball = true
            isGenerateInstaller = true
            categories = listOf("Office")
        } as Closure<LinuxConfig>
    )
    dependsOn(tasks.build)
}

tasks.register<PackageTask>("packageForMac_M1") {
    description = "package For Mac"
    platform = Platform.mac

    organizationName = organization
    organizationUrl = supportUrl

    macConfig(
        closureOf<MacConfig> {
            icnsFile = getIconFile("RedisFront.icns")
            isGenerateDmg = true
            macStartup = MacStartup.ARM64
        } as Closure<MacConfig>
    )
    dependsOn(tasks.build)
}

tasks.register<PackageTask>("packageForMac") {
    description = "package For Mac"
    platform = Platform.mac

    organizationName = organization
    organizationUrl = supportUrl

    macConfig(
        closureOf<MacConfig> {
            icnsFile = getIconFile("RedisFront.icns")
            isGenerateDmg = true
            macStartup = MacStartup.X86_64
        } as Closure<MacConfig>
    )
    dependsOn(tasks.build)
}

fun getIconFile(fileName: String): File {
    return File(projectDir.absolutePath + File.separator + "assets" + File.separator + fileName)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}
