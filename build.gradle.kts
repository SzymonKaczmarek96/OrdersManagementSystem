plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.flywaydb.flyway") version "9.12.0"
	kotlin("plugin.noarg") version "2.0.21"
	kotlin("plugin.jpa") version "2.0.21"

}

group = "Kotlin"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("org.jetbrains.kotlin:kotlin-noarg")


	runtimeOnly("org.postgresql:postgresql")
	compileOnly("org.projectlombok:lombok:0.11.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation ("org.mockito:mockito-junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")



}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}

flyway {
	locations = arrayOf("classpath:db.migration")
}

noArg {
	annotation("com.example.MyCustomAnnotation")
	invokeInitializers = true
}


