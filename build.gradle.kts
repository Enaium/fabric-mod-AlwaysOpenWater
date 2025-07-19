import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.PublishModTask

plugins {
    id("java")
}

buildscript {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
        mavenCentral()
    }

    val loomVersion: String by project
    val modPublishVersion: String by project

    dependencies {
        classpath("net.fabricmc:fabric-loom:${loomVersion}")
        classpath("me.modmuss50.mod-publish-plugin:me.modmuss50.mod-publish-plugin.gradle.plugin:${modPublishVersion}")
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(emptySet<File>())
        }
    }
}

allprojects {
    group = "cn.enaium"
    version = "1.0.1"
}

subprojects {
    apply {
        plugin("java")
        plugin("fabric-loom")
        plugin("me.modmuss50.mod-publish-plugin")
    }

    val archivesBaseName: String by project

    base {
        archivesName.set(archivesBaseName)
    }

    version = "${property("minecraft.version")}-${version}"

    tasks.processResources {
        inputs.property("currentTimeMillis", System.currentTimeMillis())

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version.toString()))
        }
    }

    repositories {
        mavenCentral()
    }

    sourceSets.main {
        resources {
            srcDir(file(rootProject.projectDir).resolve("resources"))
        }
    }

    dependencies.add("minecraft", "com.mojang:minecraft:${property("minecraft.version")}")
    dependencies.add("mappings", "net.fabricmc:yarn:${property("fabric.yarn.version")}:v2")
    dependencies.add("modImplementation", "net.fabricmc:fabric-loader:${property("fabric.loader.version")}")
    dependencies.add("modImplementation", "net.fabricmc.fabric-api:fabric-api:${property("fabric.api.version")}")

    property("java.version").toString().toInt().let {
        tasks.withType<JavaCompile> {
            options.release.set(it)
        }

        java.sourceCompatibility = JavaVersion.toVersion(it)
        java.targetCompatibility = JavaVersion.toVersion(it)
    }

    afterEvaluate {
        afterEvaluate {
            configure<ModPublishExtension> {
                file = tasks.named<AbstractArchiveTask>("remapJar").get().archiveFile.get()
                type = STABLE
                displayName = "AntiDrop ${project.version}"
                changelog = rootProject.file("changelog.md").readText(Charsets.UTF_8)
                modLoaders.add("fabric")

                curseforge {
                    projectId = "749753"
                    accessToken = providers.gradleProperty("curseforge.token")
                    minecraftVersions.add(property("minecraft.version").toString())
                    requires("fabric-api")
                }

                modrinth {
                    projectId = "vtWKK6b6"
                    accessToken = providers.gradleProperty("modrinth.token")
                    minecraftVersions.add(property("minecraft.version").toString())
                    requires("fabric-api")
                }

                github {
                    repository = "Enaium/fabric-mod-AlwaysOpenWater"
                    accessToken = providers.gradleProperty("github.token")
                    commitish = "master"
                }

                tasks.withType<PublishModTask>().configureEach {
                    dependsOn(tasks.named("remapJar"))
                }
            }
        }
    }
}