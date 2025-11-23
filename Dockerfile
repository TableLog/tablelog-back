# Eclipse Temurin 19 JDK 이미지를 베이스로 사용
# openjdk:19-jdk-slim은 더 이상 제공되지 않으므로 Temurin 사용
FROM eclipse-temurin:19-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 애플리케이션 JAR 파일을 컨테이너로 복사
COPY build/libs/tablelog-back-0.0.1-SNAPSHOT.jar /app/tablelog-back.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/tablelog-back.jar"]