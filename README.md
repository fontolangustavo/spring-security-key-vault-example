
# Spring Security Key Vault

This project is a Java Spring Boot application using PostgreSQL as the database and LocalStack for AWS services emulation.

## Requirements

Before starting, ensure you have the following installed on your machine:

- Docker
- Docker Compose

## Environment Setup

You need to create a `.env` file at the root of the project with the following AWS environment variables:

```bash
AWS_ACCESS_KEY_ID=your_access_key_id
AWS_SECRET_ACCESS_KEY=your_secret_access_key
AWS_REGION=us-east-1
```

Replace `your_access_key_id` and `your_secret_access_key` with your actual AWS credentials.

## How to Run the Project

1. Clone this repository to your local machine.
2. Ensure that Docker is running on your machine.
3. Run the following command to start the application along with PostgreSQL and LocalStack:

```bash
docker-compose up
```

Docker Compose will pull the required images, create the PostgreSQL database, set up LocalStack, and run the Java Spring Boot application.

## PostgreSQL

The project uses PostgreSQL as the database. The following environment variables are used to configure PostgreSQL within the `docker-compose.yml` file:

```yaml
POSTGRES_USER: products_db_user
POSTGRES_PASSWORD: MyP455
POSTGRES_DB: products_db
```

The `init-db.sql` script is used to initialize the database with sample data when the container starts.

## LocalStack

LocalStack is used to emulate AWS services like Secrets Manager. The configuration script `init-localstack.sh` is executed automatically to initialize LocalStack.

## Health Check

Health checks are configured for PostgreSQL and LocalStack to ensure the services are fully started before the Spring Boot application begins.

## Access the Application

After running `docker-compose up`, the Spring Boot application will be accessible at:

```
http://localhost:8080
```

Make sure all services are running properly by checking the Docker logs.

## Stopping the Application

To stop the application and remove the containers, run:

```bash
docker-compose down
```

If you want to remove all volumes (including PostgreSQL data), add the `-v` flag:

```bash
docker-compose down -v
```

## Troubleshooting

- Ensure that Docker is up and running.
- Check if the `.env` file is correctly configured.
- If any container fails to start, check the Docker logs for more details:

```bash
docker-compose logs
```