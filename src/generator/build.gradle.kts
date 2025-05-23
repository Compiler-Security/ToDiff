plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "2.1.2"

repositories {
    mavenCentral()
}

application {
    mainClass = "org.generator.tools.diffTopo.main"
}

dependencies {
    implementation("junit:junit:4.13.2")
    implementation("org.realityforge.org.jetbrains.annotations:org.jetbrains.annotations:1.7.0")
    implementation("org.graphstream:gs-core:2.0")
    implementation("commons-net:commons-net:3.9.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    implementation("commons-cli:commons-cli:1.9.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testng:testng:7.7.0")
}
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.test {
    useJUnitPlatform()
}