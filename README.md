# Server Manager GraphQL API

Server Manager is a Spring Boot GraphQL project that provides an API for managing servers. It uses a PostgreSQL database for data storage and can be easily set up using Docker. The project includes a GraphQL interface for easy querying and manipulation of server data.

## Running the Project Locally

Before you start, make sure you have the following prerequisites installed:

- [Docker](https://www.docker.com/get-started)
- Java Development Kit (JDK) 11 or above
- Apache Maven

### 1. Clone the Repository

```bash
git clone https://github.com/yogigan/server-manager.git
```
### 2. Navigate to the Root Project Directory:
    
```bash
cd server-manager
```

### 3. Start the PostgreSQL Database (via Docker)

```bash
docker-compose up -d
```

### 4. Run the Spring Boot Application

```bash
java -jar server-manager.jar
```

Access the GraphiQL interface at http://localhost:8080/graphiql in your web browser.

You can now use the GraphQL API to manage servers.

## Running Unit Tests

The Credit Simulator project includes unit tests to ensure its functionality. Follow these steps to run the unit tests:

1. **Run Tests:**
   Use Maven to compile and run the unit tests:

```sh
mvn test
```
or
```sh
./mvnw test
```
Maven will execute all the unit tests and provide you with a summary of the results.

## GraphQL API Documentation

### 1. Query: findAllServers
Retrieve a list of servers with pagination support.

Request:
```graphql
{
   findAllServers(page: 0, size: 20) {
      id
      ipAddress
      name
      memory
      type
      status
   }
}
```

### 2. Query: findServerById
Retrieve a server by its ID.

Request:
```graphql
{
  findServerById(id: 11) {
    id
    ipAddress
    name
    memory
    type
    status
  }
}
```

### 3. Query: pingServer
Ping a server by its IP address.

Request:
```graphql
{
   pingServer(ipAddress: "127.0.0.1") {
      id
      ipAddress
      name
      memory
      type
      status
   }
}
```

### 4. Mutation: createServer
Create a new server.

Request:
```graphql
mutation {
  createServer(
    server: {
      ipAddress: "127.0.0.1"
      name: "Local Server"
      memory: "16 GB"
      type: "Linux"
      status: SERVER_UP
    }
  ) {
    id
    ipAddress
    name
    memory
    type
    status
  }
}
```

### 5. Mutation: updateServer
Update server information by ID.

Request:

```graphql
mutation {
  updateServer(
    server: {
      id: 11
      ipAddress: "127.0.0.1"
      name: "Local Server Update"
      memory: "32 GB"
      type: "Virtual"
      status: SERVER_DOWN
    }
  ) {
    id
    ipAddress
    name
    memory
    type
    status
  }
}
```

### 6. Mutation: deleteServer
Delete a server by ID.

Request:
```graphql
mutation {
  deleteServer(id: 11)
}
```

### 7. Mutation: saveServers
Bulk insert servers into the database.

Request:
```graphql
mutation {
  saveServers(
    servers: [
      {
        ipAddress: "192.168.1.3",
        name: "Server 3",
        memory: "8 GB",
        type: "Virtual",
        status: SERVER_UP
      },
      {
        ipAddress: "192.168.1.4",
        name: "Server 4",
        memory: "64 GB",
        type: "Physical",
        status: SERVER_DOWN
      }
    ]
  )
}
```


## Troubleshooting

If you encounter any issues while setting up or running the project, consider the following:

- Ensure that you have Java 11 or above installed and configured correctly.
- Make sure you are running the commands from the project's root directory.
- Check for any error messages or stack traces in the console output.

## Conclusion

You've successfully set up and run the Server Manager project locally. You can now use the GraphQL API to manage servers.

For more information about GraphQL, check out the [official documentation](https://graphql.org/).

Cheers! 🚀