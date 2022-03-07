FROM azul/zulu-openjdk:17 
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/zulu17-ca
ENV JAVA_OPTS="-Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+AlwaysPreTouch"

WORKDIR /app
VOLUME /app/log
#RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser && chown -R javauser:javauser /app

COPY target/azul-neo4j-*.jar /app/azul-neo4j.jar

#USER javauser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "azul-neo4j.jar", "--spring.config.location=file:/app/config/application.properties", "$JAVA_OPTS"]
