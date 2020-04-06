import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven("https://files.minecraftforge.net/maven")
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:3.+")
    }
}

apply(plugin = "net.minecraftforge.gradle")

plugins {
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.serialization") version "1.3.71"
}

group = "me.mairwunnx.covid19"
version = "1.0.0+MC-1.14.4"

val Project.configureMinecraft
    get() = extensions.getByName<UserDevExtension>("minecraft")

val shadow: Configuration by configurations.creating
val NamedDomainObjectContainer<Configuration>.shadow: NamedDomainObjectProvider<Configuration>
    get() = named<Configuration>("shadow")

repositories {
    maven("https://libraries.minecraft.net")
    mavenCentral()
    jcenter()
}

dependencies {
    shadow(kotlin("stdlib"))
    shadow("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    minecraft("net.minecraftforge:forge:1.14.4-28.2.0")
}

configureMinecraft.mappings("snapshot", "20190719-1.14.3")

tasks.withType<Jar> {
    archiveBaseName.set(project.name)
    from(configurations.shadow.map { configuration ->
        configuration.asFileTree.fold(
            files().asFileTree
        ) { collection, file ->
            when {
                file.isDirectory -> collection
                else -> collection.plus(zipTree(file))
            }
        }
    })

    manifest {
        attributes(
            "Specification-Title" to project.name,
            "Specification-Vendor" to "MairwunNx (Pavel Erokhin)",
            "Specification-Version" to project.version,
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "MairwunNx (Pavel Erokhin)"
        )
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

fun DependencyHandler.minecraft(
    dependencyNotation: Any
): Dependency? = add("minecraft", dependencyNotation)
