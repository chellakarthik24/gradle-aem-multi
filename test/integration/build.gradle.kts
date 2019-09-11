import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java")
}

description = "Example - Integration Tests"

dependencies {
    testImplementation(project(":aem:common"))
    testImplementation(project(":aem:sites"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Build.KOTLIN_VERSION}")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:${Build.KOTLIN_VERSION}")
    testImplementation("com.intuit.karate:karate-core:0.9.4")
    testImplementation("com.intuit.karate:karate-apache:0.9.4")
    testImplementation("com.intuit.karate:karate-junit5:0.9.4")
    testImplementation("net.masterthought:cucumber-reporting:4.9.0")
}

tasks {
    named<Jar>(JavaPlugin.JAR_TASK_NAME) {
        enabled = false
    }

    named<Task>(LifecycleBasePlugin.CHECK_TASK_NAME) {
        dependsOn.remove(named(JavaPlugin.TEST_TASK_NAME))
    }

    named<Test>(JavaPlugin.TEST_TASK_NAME) {
        outputs.upToDateWhen { false }

        testLogging {
            showStandardStreams = true
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        }

        systemProperty("karate.options", System.getProperty("karate.options"))
        systemProperty("karate.env", System.getProperty("karate.env"))
        systemProperty("karate.parallel", System.getProperty("karate.parallel"))
    }

    withType<KotlinCompile>().configureEach {
        destinationDir = file("build/classes/java")
    }
}

sourceSets {
    test {
        resources {
            srcDir(file("src/test/java"))
            exclude("**/*.kt", "**/*.java")
        }
    }
}
