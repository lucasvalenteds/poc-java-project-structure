dependencies {
    implementation(project(":user-service-contract"))
    implementation(project(":user-service-shared"))

    implementation("com.fasterxml.jackson.core", "jackson-core", properties["version.jackson"].toString())
    implementation("com.fasterxml.jackson.core", "jackson-databind", properties["version.jackson"].toString())
    implementation(
        "com.fasterxml.jackson.datatype",
        "jackson-datatype-jsr310",
        properties["version.jackson"].toString()
    )

    testImplementation("org.eclipse.jetty", "jetty-webapp", properties["version.jetty"].toString())
    testImplementation(project(":user-service-shared"))
    testImplementation(project(":user-service-server")) {
        exclude("org.springframework.boot")
    }

    // Persistence
    testImplementation("org.springframework.data", "spring-data-jdbc", properties["version.spring.data"].toString())
    testImplementation("mysql", "mysql-connector-java", properties["version.mysql"].toString())
    testImplementation("org.flywaydb", "flyway-core", properties["version.flyway"].toString())

    // REST API
    testImplementation("javax.servlet", "javax.servlet-api", properties["version.servlet"].toString())
    testImplementation("org.springframework", "spring-core", properties["version.spring.framework"].toString())
    testImplementation("org.springframework", "spring-context", properties["version.spring.framework"].toString())
    testImplementation("org.springframework", "spring-webmvc", properties["version.spring.framework"].toString())
    testImplementation("org.springframework", "spring-test", properties["version.spring.framework"].toString())
}