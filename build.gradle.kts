plugins {
    id("application")
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

application {
    // Set the main class using the correct syntax for Kotlin DSL
    mainClass.set("org.example.KVServer")
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Run the KVClient"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.example.KVClient")
}

/*tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run the KVServer"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("org.example.KVServer")
}*/

tasks.test {
    useJUnitPlatform()
}

