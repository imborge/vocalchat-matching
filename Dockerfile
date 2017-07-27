FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/vocalchat-matching.jar /vocalchat-matching/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/vocalchat-matching/app.jar"]
