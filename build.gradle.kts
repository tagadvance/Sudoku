plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.+")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:3.+")
    testImplementation("com.google.guava:guava-testlib:33.4.8-jre")

    api("org.slf4j:slf4j-api:2.0.17")

    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.google.guava:guava:33.4.8-jre")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
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
