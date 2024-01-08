import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
}

allprojects {
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        mavenLocal()
        mavenCentral()
    }
}

group = "org.dromara"
version = "1.0.7"

if (JavaVersion.current() < JavaVersion.VERSION_17)
    throw RuntimeException("compile required Java ${JavaVersion.VERSION_17}, current Java ${JavaVersion.current()}")


println()
println("-------------------------------------------------------------------------------")
println("$name Version: $version")
println("Project Path:  $projectDir")
println("Java Version:  ${System.getProperty("java.version")}")
println("Gradle Version: ${gradle.gradleVersion} at ${gradle.gradleHomeDir}")
println("Current Date:  ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}")
println("-------------------------------------------------------------------------------")
println()

allprojects {
    tasks {

        withType<JavaCompile>().configureEach {
            sourceCompatibility = "17"
            targetCompatibility = "17"
            options.encoding = "utf-8"
            options.isDeprecation = false
        }

        withType<Jar>().configureEach {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE

            manifest {
                attributes("Implementation-Vendor" to "www.redisfront.com")
                attributes("Implementation-Copyright" to "redisfront")
                attributes("Implementation-Version" to project.version)
                attributes("Multi-Release" to "true")
            }

            from("${rootDir}/LICENSE") {
                into("META-INF")
            }
        }


    }

}


