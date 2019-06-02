import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
    application
}

group = "cc.t0ast.taskpipe"
version = "0.1-SNAPSHOT"
application.mainClassName = "cc.t0ast.taskpipe.cli.Main"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile(project(":runner"))
    compile("com.xenomachina:kotlin-argparser:2.0.7")
    testCompile("junit", "junit", "4.9")
    testCompile("com.github.stefanbirkner:system-rules:1.19.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest.attributes.apply {
        put("Main-Class", application.mainClassName)
    }
    from(configurations.runtime.map { if (it.isDirectory) it else zipTree(it) })
}
