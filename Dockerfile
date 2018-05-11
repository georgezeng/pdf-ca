FROM java:8
COPY pdf-ca/target/pdf-ca-0.0.1-SNAPSHOT.jar ./pdf-ca.jar
CMD java -jar pdf-ca.jar
EXPOSE 80