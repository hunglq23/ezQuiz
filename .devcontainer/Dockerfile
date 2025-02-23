FROM maven:3.9.6-eclipse-temurin-17

# Set working directory
WORKDIR /workspace

# Install MySQL Client (optional)
RUN apt-get update && apt-get install -y mysql-client && rm -rf /var/lib/apt/lists/*

CMD [ "sleep", "infinity" ]