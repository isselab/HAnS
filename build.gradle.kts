import org.jetbrains.changelog.Changelog // Gradle Changelog Plugin
import org.jetbrains.changelog.markdownToHTML // Gradle Changelog Plugin
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType // Gradle IntelliJ Plugin

plugins {
    id("java") // Java support
    id("idea")
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // Gradle IntelliJ Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.grammarKit) // Gradle GrammarKit Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(21)
}

// Configure project's dependencies
repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.opentest4j)

    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        // Module Dependencies. Uses `platformBundledModules` property from the gradle.properties file for bundled IntelliJ Platform modules.
        bundledModules(providers.gradleProperty("platformBundledModules").map { it.split(',') })

        testFramework(TestFrameworkType.Platform)
    }
}

idea {
    module {
        val main by java.sourceSets
        val genDir = "src/main/gen"
        generatedSourceDirs.add(file(genDir))
        main.java.srcDirs(file(genDir))
    }
}

intellijPlatform {
    pluginConfiguration {
        id = providers.gradleProperty("pluginId")
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false)
                        .withLinks(true)
                        .withSummary(true),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion").map {
            listOf(
                it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    pluginVerification {
        ides {
            recommended()
            create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))
        }
    }
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

// GrammarKit generation tasks — run before compilation when .bnf or .flex files change
val generateFeatureModelParser by tasks.registering(GenerateParserTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureModel/FeatureModel.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("se/isselab/HAnS/featureModel/parser/FeatureModelParser.java")
    pathToPsiRoot.set("se/isselab/HAnS/featureModel/psi")
}

val generateFeatureModelLexer by tasks.registering(GenerateLexerTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureModel/FeatureModel.flex")
    targetDir.set("src/main/gen/se/isselab/HAnS/featureModel")
    skeleton.set("idea-flex.skeleton")
}

val generateFeatureModelHighlightingLexer by tasks.registering(GenerateLexerTask::class) {
    source.set("src/main/java/se/isselab/HAnS/syntaxHighlighting/featureModel/FeatureModelHighlightingLexer.flex")
    targetDir.set("src/main/gen/se/isselab/HAnS/featureModel")
    skeleton.set("idea-flex.skeleton")
}

val generateCodeAnnotationParser by tasks.registering(GenerateParserTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/codeAnnotation/CodeAnnotation.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("se/isselab/HAnS/featureAnnotation/codeAnnotation/parser/CodeAnnotationParser.java")
    pathToPsiRoot.set("se/isselab/HAnS/featureAnnotation/codeAnnotation/psi")
}

val generateCodeAnnotationLexer by tasks.registering(GenerateLexerTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/codeAnnotation/CodeAnnotation.flex")
    targetDir.set("src/main/gen/se/isselab/HAnS/featureAnnotation/codeAnnotation")
    skeleton.set("idea-flex.skeleton")
}

val generateFileAnnotationParser by tasks.registering(GenerateParserTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/fileAnnotation/FileAnnotation.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("se/isselab/HAnS/featureAnnotation/fileAnnotation/parser/FileAnnotationParser.java")
    pathToPsiRoot.set("se/isselab/HAnS/featureAnnotation/fileAnnotation/psi")
}

val generateFileAnnotationLexer by tasks.registering(GenerateLexerTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/fileAnnotation/FileAnnotation.flex")
    targetDir.set("src/main/gen/se/isselab/HAnS/featureAnnotation/fileAnnotation")
    skeleton.set("idea-flex.skeleton")
}

val generateFolderAnnotationParser by tasks.registering(GenerateParserTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/folderAnnotation/FolderAnnotation.bnf")
    targetRoot.set("src/main/gen")
    pathToParser.set("se/isselab/HAnS/featureAnnotation/folderAnnotation/parser/FolderAnnotationParser.java")
    pathToPsiRoot.set("se/isselab/HAnS/featureAnnotation/folderAnnotation/psi")
}

val generateFolderAnnotationLexer by tasks.registering(GenerateLexerTask::class) {
    source.set("src/main/java/se/isselab/HAnS/featureAnnotation/folderAnnotation/FolderAnnotation.flex")
    targetDir.set("src/main/gen/se/isselab/HAnS/featureAnnotation/folderAnnotation")
    skeleton.set("idea-flex.skeleton")
}

val allGenerateTasks = listOf(
    generateFeatureModelParser, generateFeatureModelLexer, generateFeatureModelHighlightingLexer,
    generateCodeAnnotationParser, generateCodeAnnotationLexer,
    generateFileAnnotationParser, generateFileAnnotationLexer,
    generateFolderAnnotationParser, generateFolderAnnotationLexer
)

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    publishPlugin {
        dependsOn("patchChangelog")
    }

    compileJava {
        dependsOn(allGenerateTasks)
    }
}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}