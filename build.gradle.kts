import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.LinkMapping
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.jfrog.bintray") version "1.8.4"
    id("org.jetbrains.dokka") version "0.9.18"
    kotlin("jvm") version "1.3.21"
    java
    `maven-publish`
}

group = "me.schlaubi"
version = "1.0"

// Variables
val slf4jVersion = "1.7.26"
val okHttpVersion = "3.14.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    // HTTP
    compile("com.squareup.okhttp3", "okhttp", okHttpVersion)

    // JSON
    compile("org.json", "json", "20180813")

    // Logging
    compile("org.slf4j", "slf4j-api", slf4jVersion)

    // Util
    compile("com.google.guava:guava:27.1-jre")
    
    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // API Wrappers
    @Suppress("SpellCheckingInspection")
    implementation("net.dv8tion:JDA:4.ALPHA.0_67")
    implementation("com.discord4j:discord4j-core:3.0.1")

    // Tests
    testCompile("junit", "junit", "4.12")
    testCompile("org.slf4j", "slf4j-simple", slf4jVersion)
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    setPublications("mavenJava")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "discordstats"
        userOrg = "drschlaubi"
        setLicenses("GPL-3.0")
        vcsUrl = "https://github.com/DRSchlaubi/discordstats.git"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as String
        })
    })
}

val dokkaJar by tasks.creating(Jar::class)

val sourcesJar by tasks.creating(Jar::class)

artifacts {
    add("archives", sourcesJar)
    add("archives", dokkaJar)
}

tasks {
    dokka {
        outputFormat = "html"
        outputDirectory = "docs"
        jdkVersion = 8
        reportUndocumented = true
        impliedPlatforms = mutableListOf("JVM")
        sourceDirs = files("src/main/kotlin", "src/main/java")
        sourceDirs.forEach {
            val relativePath = rootDir.toPath().relativize(it.toPath()).toString()
            linkMapping(delegateClosureOf<LinkMapping> {
                dir = it.absolutePath
                url = "https://github.com/DRSchlaubi/discordstats/tree/master/$relativePath"
                suffix = "#L"
            })
        }
        externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
            url = uri("https://www.slf4j.org/api/").toURL()
        })
        externalDocumentationLink(delegateClosureOf<DokkaConfiguration.ExternalDocumentationLink.Builder> {
            url = uri("https://square.github.io/okhttp/3.x/okhttp/").toURL()
        })
    }
    "sourcesJar"(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    "dokkaJar"(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        archiveClassifier.set("javadoc")
        from(dokka)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}