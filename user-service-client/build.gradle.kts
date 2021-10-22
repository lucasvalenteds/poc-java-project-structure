dependencies {
    implementation(project(":user-service-contract"))
    implementation(project(":user-service-shared"))

    implementation("com.fasterxml.jackson.core", "jackson-databind", properties["version.jackson"].toString())
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", properties["version.jackson"].toString())
}