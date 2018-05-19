FROM java:8
ADD /root/app/pdf-ca/target/pdf-ca-0.0.1-SNAPSHOT.jar ./pdf-ca.jar
CMD java -jar pdf-ca.jar
EXPOSE 80