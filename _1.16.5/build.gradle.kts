version = "${property("minecraft.version")}-${version}"

tasks.withType<JavaCompile> {
    options.release.set(8)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}