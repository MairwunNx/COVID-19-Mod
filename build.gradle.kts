/**
 * Build script for forge 28.X.X and minecraft 1.14.4.
 *
 * If you have any questions or encounter a problem, be sure to open an issue
 * https://github.com/MairwunNx/Minecraft-Forge-Kotlin-Mod-Template/issues/new/choose
 */

import net.minecraftforge.gradle.userdev.UserDevExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

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
    eclipse
    `maven-publish`
}

group = "com.yourname.modid"

/*
    The version of your mod, please use versions that meet
    the requirements of semver, do not use versions of the
    type 1.14.4-1.0.0 and the like, the following is a correct
    example of a version that meets the standards of semver.

    Read more about it: https://semver.org/
*/
version = "1.0.0-DEV-SNAPSHOT+MC-1.14.4"

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
    /*
        This dependency will be shadowed in jar,
        if we use compileFatMod task.
     */
    shadow(kotlin("stdlib"))
    implementation(kotlin("stdlib"))

    /*
        Specify the version of Minecraft to use, If this is
        any group other then 'net.minecraft' it is assumed
        that the dep is a ForgeGradle 'patcher' dependency.
        And it's patches will be applied. The userdev artifact is
        a special name and will get all sorts of transformations applied to it.
    */
    minecraft("net.minecraftforge:forge:1.14.4-28.2.0")

    /*
        Uncomment line below if your project will be register
        or interacting with in-game commands.

        This dependency not need to shadow, it dependency
        already included in minecraft.
     */
    // implementation("com.mojang:brigadier:1.0.17")

    /*
        Uncomment line below if your project will be using tests.
     */
    // testImplementation(kotlin("test-junit5"))

    /*
        For more info visit:
            https://docs.gradle.org/current/userguide/dependency_management_for_java_projects.html
            https://docs.gradle.org/current/userguide/dependency_management.html
     */
}

val runProperties = mapOf(
    // Recommended logging data for a userdev environment.
    Pair("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP"),
    // Recommended logging level for the console.
    Pair("forge.logging.console.level", "debug")
)

configureMinecraft.let {
    /*
        The mappings can be changed at any time, and must be in the following format.
        snapshot_YYYYMMDD   Snapshot are built nightly.
        stable_#            Stables are built at the discretion of the MCP team.
        Use non-default mappings at your own risk. they may not always work.
        Simply re-run your setup task after changing the mappings to update your workspace.
    */
    it.mappings("snapshot", "20190719-1.14.3")

    /*
        Uncomment line below if you want to use access transformer with specified files.
     */
    // it.accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

    /*
        Default run configurations. These can be tweaked, removed, or duplicated as needed.
     */
    it.runs.create("client") {
        this.properties(runProperties)
        this.workingDirectory(File(".${File.separatorChar}run"))
        this.mods {
            this.create("examplemod") {
                this.source(sourceSets.getByName("main"))
            }
        }
    }
    it.runs.create("server") {
        this.properties(runProperties)
        this.workingDirectory(File(".${File.separatorChar}run"))
        this.mods {
            this.create("examplemod") {
                this.source(sourceSets.getByName("main"))
            }
        }
    }
    it.runs.create("data") {
        this.properties(runProperties)
        this.workingDirectory(File(".${File.separatorChar}run"))
        this.args(
            "--mod", "examplemod", "--all", "--output",
            file("src/generated/resources/")
        )
        this.mods {
            this.create("examplemod") {
                this.source(sourceSets.getByName("main"))
            }
        }
    }
}

tasks.withType<Jar> {
    /*
        Mod archive file name, example, if you set this value
        `HelloMod` you will see file name as `HelloMod-1.0.0-DEV-SNAPSHOT+MC-1.14.4.jar`.
     */
    archiveBaseName.set(project.name)

    /*
        Shadowing dependencies what has configuration `shadow`.
        Remove it if you not need embedding dependencies in jar.
     */
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

    /*
        Example for how to get properties into the manifest for reading by the runtime.
     */
    manifest {
        attributes(
            "Specification-Title" to project.name,
            "Specification-Vendor" to "examplemodvendor",
            "Specification-Version" to project.version,
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "examplemodvendor"
        )
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.languageVersion = "1.3"
}

publishing {
    repositories {
        maven {
            /*
                Replace url in URI class constructor to your URL.
             */
            url = URI("https://github.com/MairwunNx/Minecraft-Forge-Kotlin-Mod-Template")
            credentials {
                username = System.getenv("GradleUser")
                password = System.getenv("GradlePass")
            }
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

fun DependencyHandler.deobf(
    dependencyNotation: Any
): Dependency? = add("deobf", dependencyNotation)

fun DependencyHandler.minecraft(
    dependencyNotation: Any
): Dependency? = add("minecraft", dependencyNotation)
