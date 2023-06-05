# Base image with Java and SBT
FROM mozilla/sbt as builder

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Build the project
RUN sbt clean package

# Copy the file from the build directory to the Docker image
COPY ./metropolis_visits_iso8601.csv ./metropolis_visits_iso8601.csv

ENTRYPOINT ["sh", "-c", "sbt 'run metropolis_visits_iso8601.csv' 2> /dev/null"]
