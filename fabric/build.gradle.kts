plugins {
    java
    id("fabric-loom") version "1.13.3"
    id("maven-publish")
}

loom {
    enableModProvidedJavadoc.set(false)
    mixin {
        useLegacyMixinAp.set(true)
        add(sourceSets["main"], "ominouschestlock.refmap.json")
        defaultRefmapName.set("ominouschestlock.refmap.json")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of((findProperty("javaVersion") as String).toInt()))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraftVersion")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("fabricLoaderVersion")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabricApiVersion")}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-data-generation-api-v1")
    }
    annotationProcessor("net.fabricmc:sponge-mixin:0.17.0+mixin.0.8.7")
    implementation(project(":common"))
    implementation("org.yaml:snakeyaml:2.2")
    include("org.yaml:snakeyaml:2.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "mod_name" to ((findProperty("pluginName") as String?) ?: "OminousChestLock"),
                "minecraftVersion" to project.property("minecraftVersion"),
                "fabricLoaderVersion" to project.property("fabricLoaderVersion"),
                "fabricApiVersion" to project.property("fabricApiVersion")
            )
        )
    }
}

tasks.jar {
    archiveBaseName.set((findProperty("pluginName") as String?) ?: "OminousChestLock")
}

tasks.remapJar {
    val baseName = (findProperty("pluginName") as String?) ?: "OminousChestLock"
    archiveFileName.set("${baseName}-${project.version}_Fabric.jar")
}
