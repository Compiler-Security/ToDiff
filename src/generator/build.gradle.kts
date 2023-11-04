plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("junit:junit:4.13.2")
    implementation("org.realityforge.org.jetbrains.annotations:org.jetbrains.annotations:1.7.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testng:testng:7.7.0")
}

tasks.test {
    useJUnitPlatform()
}