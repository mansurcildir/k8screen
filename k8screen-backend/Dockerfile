FROM eclipse-temurin:21-alpine

RUN addgroup k8screen && adduser --disabled-password --no-create-home --ingroup k8screen k8screen
USER k8screen

LABEL org.opencontainers.image.description="K8screen Backend"
ADD target/k8screen-backend.jar k8screen-backend.jar
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/k8screen-backend.jar", "1>/dev/stdout", "2>/dev/stderr"]
