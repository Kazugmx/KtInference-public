plugins {
    kotlin("jvm") version "1.9.22"
}

group = "org.guardmantokai"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
val djlVersion = "0.26.0"
dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("ai.djl:djl-zero:$djlVersion")
    implementation("ai.djl.tensorflow:tensorflow-api:$djlVersion")
    implementation("ai.djl.tensorflow:tensorflow-engine:$djlVersion")
    implementation("ai.djl.tensorflow:tensorflow-model-zoo:$djlVersion")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.12")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}