plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of((findProperty("javaVersion") as String).toInt()))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
