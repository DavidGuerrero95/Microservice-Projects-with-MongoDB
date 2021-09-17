FROM openjdk:15
VOLUME /tmp
ADD ./target/springboot-proyectos-0.0.1-SNAPSHOT.jar proyectos.jar
ENTRYPOINT ["java","-jar","/proyectos.jar"]