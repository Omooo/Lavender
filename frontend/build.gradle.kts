plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.343")

    implementation(enforcedPlatform(kotlinw("wrappers-bom:1.0.0-pre.477")))

    implementation(kotlinw("react"))
    implementation(kotlinw("react-dom"))
    implementation(kotlinw("react-router-dom"))

    implementation(kotlinw("emotion"))
    implementation(kotlinw("mui"))
    implementation(kotlinw("mui-icons"))

    implementation(npm("date-fns", "2.29.3"))
    implementation(npm("@date-io/date-fns", "2.16.0"))
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable()
    }
}

// 注册 browserPackage Task
tasks.register("browserPackage") {
    dependsOn("browserDistribution")
    mustRunAfter("browserDistribution")
    doLast {
        val rootDir = buildDir.resolve("distributions")
        val html = rootDir.resolve("index.html").readText()
        val javascript = rootDir.resolve("frontend.js").readText()
        val reportFile = rootDir.resolve("report.html")
        reportFile.writeText(
            html.replace("<script src=\"frontend.js\"></script>", "<script>$javascript</script>")
        )
        println("Wrote HTML report to ${reportFile.toPath().toUri()}")
    }
}
