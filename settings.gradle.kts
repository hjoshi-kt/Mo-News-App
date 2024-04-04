pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("moengage") {
            from("com.moengage:android-dependency-catalog:4.1.2")
        }
    }
}

rootProject.name = "News App"
include(":app")
