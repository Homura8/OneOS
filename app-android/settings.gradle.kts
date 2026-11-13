import java.io.File
import java.util.Properties

// Credentials for the tribalfs One UI / SESL GitHub Packages repositories. Local
// credentials live outside the checkout under ~/.config/twidget. CI falls
// back to environment variables.
val githubPropertiesFile = File(
    System.getProperty("user.home"),
    ".config/twidget/github.properties",
)
val githubProperties = Properties().apply {
    githubPropertiesFile.takeIf { it.isFile }?.inputStream()?.use { load(it) }
}
val ghPackagesUser: String =
    githubProperties.getProperty("ghUsername") ?: System.getenv("GH_PACKAGES_USER") ?: ""
val ghPackagesToken: String =
    githubProperties.getProperty("ghAccessToken") ?: System.getenv("GH_PACKAGES_TOKEN") ?: ""

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// The pinned tribalfs One UI / SESL libraries are on GitHub Packages, not
// Maven Central. Keep them in their publishers' repositories so a failed
// lookup cannot fall through to JitPack or an unrelated package registry.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        exclusiveContent {
            forRepository {
                maven("https://maven.pkg.github.com/tribalfs/oneui-design") {
                    name = "TribalfsOneUiDesign"
                    credentials { username = ghPackagesUser; password = ghPackagesToken }
                }
            }
            filter { includeModule("io.github.tribalfs", "oneui-design") }
        }
        exclusiveContent {
            forRepository {
                maven("https://maven.pkg.github.com/tribalfs/sesl-androidx") {
                    name = "TribalfsSeslAndroidx"
                    credentials { username = ghPackagesUser; password = ghPackagesToken }
                }
            }
            filter { includeGroupByRegex("sesl\\.androidx\\..*") }
        }
        exclusiveContent {
            forRepository {
                maven("https://maven.pkg.github.com/tribalfs/sesl-material-components-android") {
                    name = "TribalfsSeslMaterial"
                    credentials { username = ghPackagesUser; password = ghPackagesToken }
                }
            }
            filter { includeGroup("sesl.com.google.android.material") }
        }
    }
}

rootProject.name = "OneOS"

include(":app")
// OneOS inside the guest: "Back to One UI" (guest/build.gradle.kts).
include(":guest")
include(":core:model")
include(":core:device")
include(":core:root")
include(":core:safety")
