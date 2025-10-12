# OpenJDK 19 이미지를 베이스로 사용
FROM openjdk:19-jdk-slim

# 애플리케이션 JAR 파일을 컨테이너로 복사
COPY build/libs/tablelog-back-0.0.1-SNAPSHOT.jar /app/tablelog-back.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/tablelog-back.jar"]