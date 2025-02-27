import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

kotlin {
    jvmToolchain(17)
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

group = "com.github.ostafen"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.repsy.io/mvn/chrynan/public") } // Kotlin SecureRandom
}

val dokkaVersion: String by project
val kotlinVersion: String by project
val kotlinxSerializationJsonVersion: String by project
val kotlinxCoroutinesVersion: String by project
val ktorVersion: String by project
val kotlinExtlibVersion: String by project
val kotlinxDateTimeVersion: String by project
val okioVersion: String by project
val sublabCommonVersion: String by project
val sublabScaleVersion: String by project
val sublabHashingVersion: String by project
val sublabEncryptingVersion: String by project

dependencies {
    testImplementation(kotlin("test"))
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:$dokkaVersion")
    dokkaJavadocPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:$dokkaVersion")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    // Kotlin X
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")

    // Ktor
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")

    // Kotlin platform
    implementation("org.kotlinextra:kotlin-extlib-jvm:$kotlinExtlibVersion") // TODO: resolve to all platforms
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")
    implementation("com.squareup.okio:okio:$okioVersion")

    // Sublab
    implementation("dev.sublab:common-kotlin:$sublabCommonVersion")
    implementation("dev.sublab:scale-codec-kotlin:$sublabScaleVersion")
    implementation("dev.sublab:hashing-kotlin:$sublabHashingVersion")
    implementation("dev.sublab:encrypting-kotlin:$sublabEncryptingVersion")

    implementation("dev.sublab:sr25519-kotlin:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.dokkaHtml.configure {
    outputDirectory.set(projectDir.resolve("reference"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn("dokkaJavadoc")
    from("$buildDir/dokka/javadoc")
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        register("jitpack", MavenPublication::class) {
            groupId = groupId
            artifactId = rootProject.name
            version = version
            from(components["kotlin"])
        }
    }
}