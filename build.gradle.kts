plugins {
    `java-library`
    `maven-publish`
    signing
    id("net.ltgt.errorprone") version "5.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.google.guava:guava-bom:33.7.1-jre"))
    implementation("com.google.guava:guava")
    implementation("org.apache.commons:commons-math3:3.6.1")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(platform("org.mockito:mockito-bom:5.23.0"))
    testImplementation("org.mockito:mockito-core")

    testImplementation("com.google.guava:guava-testlib")

    // 2.42.0 is the last release that runs on a Java 17 JVM; raising it
    // requires raising the toolchain first.
    errorprone("com.google.errorprone:error_prone_core:2.42.0")
}

// CI builds against each supported LTS; the default is the project floor.
val javaVersion = (findProperty("javaVersion") as String? ?: "17").toInt()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:all")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

group = "com.tagadvance"
version = "1.0.0"

java {
    //withSourcesJar()
    //withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "sudoku"
            from(components["java"])

            pom {
                name.set("Sudoku")
                description.set("...")
                url.set("https://github.com/tagadvance/Sudoku")


                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/tagadvance/Sudoku/blob/master/LICENSE")
                    }
                }

                organization {
                    name.set("tagadvance")
                    url.set("https://tagadvance.com")
                }

                developers {
                    developer {
                        id.set("tagadvance")
                        name.set("Tag Spilman")
                        email.set("tagadvance+Sudoku@gmail.com")
                        organization.set("tagadvance")
                        organizationUrl.set("https://tagadvance.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/tagadvance/Sudoku.git")
                    developerConnection.set("scm:git:ssh://git@github.com/tagadvance/Sudoku.git")
                    url.set("https://github.com/tagadvance/Sudoku")
                }
            }
        }
    }

    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
            name = "SonatypeSnapshot"
            credentials {
                username = System.getenv("SONATYPE_USER")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            name = "SonatypeStaging"
            credentials {
                username = System.getenv("SONATYPE_USER")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_SIGNING_KEY")
    val signingPassword = System.getenv("GPG_SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}
