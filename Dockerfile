FROM openjdk:11

COPY target/credit-recovery-bridge-*.jar /usr/src/app/credit-recovery-bridge.jar

WORKDIR /usr/src/app

EXPOSE 9292

ENTRYPOINT ["java", "-jar", "credit-recovery-bridge.jar"]
