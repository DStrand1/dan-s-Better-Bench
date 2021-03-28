import net.minecraftforge.gradle.user.UserBaseExtension

buildscript {
    repositories {
        mavenCentral()
        maven {
            name = "forge"
            setUrl("http://files.minecraftforge.net/maven")
        }
    }
    dependencies {
        classpath("net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT")
    }
}

apply {
    plugin("net.minecraftforge.gradle.forge")
}

val mcVersion = "1.12.2"
val modVersion = getVersionFromJava(file("src/main/java/dan/betterbench/BetterBench.java"))

version = "$mcVersion-$modVersion"
group="betterbench"

configure<BasePluginConvention> {
    archivesBaseName = "betterbench"
}

fun minecraft(configure : UserBaseExtension.() -> Unit) = project.configure(configure)

minecraft {
    version = "$mcVersion-14.23.5.2847"
    mappings = "stable_39"
    runDir = "run"
}

repositories {
    maven {
        name = "JEI Maven"
        setUrl("http://dvs1.progwml6.com/files/maven/")
    }
}

dependencies {
    "deobfCompile"("mezz.jei:jei_$mcVersion:+")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val processResources: ProcessResources by tasks
val sourceSets: SourceSetContainer = the<JavaPluginConvention>().sourceSets

processResources.apply {
    inputs.property("version", modVersion)
    inputs.property("mcVersion", mcVersion)

    from(sourceSets["main"].resources.srcDirs) {
        include("mcmod.info")
        expand(mapOf("version" to modVersion,
            "mcversion" to mcVersion))
    }

    from(sourceSets["main"].resources.srcDirs) {
        exclude("mcmod.info")
    }
    rename("(.+_at.cfg)", "META-INF/$1")
}

val jar: Jar by tasks
jar.apply {
    manifest {
        attributes(mapOf("FMLat" to "betterbench_at.cfg",
            "FMLCorePlugin" to "dan.betterbench.asm.BBLoadingPlugin"))
    }
}

val sourceTask: Jar = tasks.create("source", Jar::class.java) {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

artifacts {
    add("archives", jar)
    add("archives", sourceTask)
}

fun getVersionFromJava(file: File): String {
    var version = "0"
    val prefix = "public static final int"

    file.forEachLine { line ->
        var s = line.trim()
        if (s.startsWith(prefix)) {
            s = s.substring(prefix.length, s.length - 1)
            s = s.replace(" = \"", " ").replace("\"", " ").trim()
            val pts = s.split(" ")

            when {
                pts[0] == "VERSION" -> version = pts[1]
            }
        }
    }
    return version
}