plugins {
    id("java")
    id("application")
}

group = "com.zevin"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.zevin.jdk.bitset.BitSetApiExample")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
