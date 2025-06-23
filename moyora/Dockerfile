# 1. JDK 21 베이스 이미지
FROM openjdk:21

# 2. 포트 8080 오픈
EXPOSE 8080

# 3. 환경변수 명시
ENV PORT=8080

# 4. JAR 파일을 이미지에 복사
COPY build/libs/moyora-0.0.1-SNAPSHOT.jar app.jar

# 5. Spring Boot 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
