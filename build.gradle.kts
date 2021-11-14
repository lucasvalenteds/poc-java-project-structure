plugins {
    java
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.example.shared")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.apache.logging.log4j", "log4j-api", properties["version.log4j"].toString())
        implementation("org.apache.logging.log4j", "log4j-core", properties["version.log4j"].toString())
        implementation("org.slf4j", "slf4j-simple", properties["version.slf4j"].toString())

        compileOnly("org.projectlombok", "lombok", properties["version.lombok"].toString())
        annotationProcessor("org.projectlombok", "lombok", properties["version.lombok"].toString())
        testCompileOnly("org.projectlombok", "lombok", properties["version.lombok"].toString())
        testAnnotationProcessor("org.projectlombok", "lombok", properties["version.lombok"].toString())

        testImplementation("org.junit.jupiter", "junit-jupiter", properties["version.junit"].toString())
        testImplementation("org.assertj", "assertj-core", properties["version.assertj"].toString())
        testImplementation("org.mockito", "mockito-core", properties["version.mockito"].toString())
    }
}