dependencies {
    implementation(project(":user-service-contract"))

    // Persistence
    implementation("org.springframework.data", "spring-data-jdbc", properties["version.spring.data"].toString())
    implementation("mysql", "mysql-connector-java", properties["version.mysql"].toString())
    implementation("org.flywaydb", "flyway-core", properties["version.flyway"].toString())

    // REST API
    implementation("com.fasterxml.jackson.core", "jackson-core", properties["version.jackson"].toString())
    implementation("com.fasterxml.jackson.core", "jackson-databind", properties["version.jackson"].toString())
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", properties["version.jackson"].toString())
    implementation("javax.servlet", "javax.servlet-api", properties["version.servlet"].toString())
    implementation("org.springframework", "spring-core", properties["version.spring.framework"].toString())
    implementation("org.springframework", "spring-context", properties["version.spring.framework"].toString())
    implementation("org.springframework", "spring-webmvc", properties["version.spring.framework"].toString())
    implementation("org.springframework", "spring-webflux", properties["version.spring.framework"].toString())
    testImplementation("org.springframework", "spring-test", properties["version.spring.framework"].toString())
    testImplementation("com.jayway.jsonpath", "json-path", properties["version.jsonpath"].toString())
    testImplementation("com.github.tomakehurst", "wiremock-jre8", properties["version.wiremock"].toString())

    // Web Server
    implementation("org.eclipse.jetty", "jetty-servlets", properties["version.jetty"].toString())
    implementation("org.eclipse.jetty", "jetty-webapp", properties["version.jetty"].toString())
}