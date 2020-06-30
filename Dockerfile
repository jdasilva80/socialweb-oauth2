FROM openjdk:8
VOLUME /tmp
EXPOSE 8009
ADD ./target/socialweb-oauth2-0.0.1-SNAPSHOT.jar socialweb-oauth2.jar
ENTRYPOINT ["java","-jar","/socialweb-oauth2.jar"]