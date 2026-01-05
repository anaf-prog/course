# STAGE 1: BUILD
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /build
COPY . .
RUN chmod +x mvnw
# Build aplikasi (ini akan menghasilkan folder target/course-0.0.1-SNAPSHOT/)
RUN ./mvnw clean package -DskipTests

# STAGE 2: RUN
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# PENTING: Copy isi folder hasil assembly
# Ini agar .jar, lib, dan config berada tepat di /app/
COPY --from=build /build/target/course-0.0.1-SNAPSHOT/ .

# Beri izin akses (opsional tapi baik untuk Linux)
RUN chmod +x course-0.0.1-SNAPSHOT.jar

EXPOSE 8085

# Menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "course-0.0.1-SNAPSHOT.jar"]