plugins {
	java
	id("org.springframework.boot") version "3.1.3"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "ms_312"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

// Build executable jar
tasks.jar {
	enabled = false
	// Remove `plain` postfix from jar file name
	archiveClassifier.set("")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.json:json:20230618")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	//implementation("io.springfox:springfox-boot-starter:3.0.0")
	//implementation("javax.servlet:javax.servlet-api:3.1.0")
	implementation("io.springfox:springfox-swagger-ui:2.9.2");

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("junit:junit:4.13.1")


}

tasks.withType<Test> {
	useJUnitPlatform()
}
