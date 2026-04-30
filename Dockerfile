FROM tomcat:9.0-jdk21-temurin

# Clean default apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy your WAR
COPY ExpenseSplitter.war /usr/local/tomcat/webapps/ROOT.war

# Copy MySQL driver (optional if already inside WAR)
ADD https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.6.0/mysql-connector-j-9.6.0.jar \
    /usr/local/tomcat/lib/

EXPOSE 8080

CMD ["catalina.sh", "run"]