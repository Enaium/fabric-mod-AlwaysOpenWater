version = "${property("minecraft.version")}-${version}"

tasks.withType<JavaCompile> {
    options.release.set(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}