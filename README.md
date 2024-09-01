# aws-ts-starter
A beginner-friendly AWS serverless application template built with Node.js and TypeScript using serverless framework. Ideal for learning and quick project setup, featuring best practices and easy deployment using AWS services like Lambda, API Gateway, and DynamoDB.

## Features

- Serverless architecture using AWS Lambda and API Gateway
- DynamoDB for data storage
- TypeScript for type-safe development
- Serverless Framework for easy deployment and management
- Local development environment with offline support

## Prerequisites

- Node.js (version 18.x or later)
- AWS CLI configured with your credentials
- Docker (optional for local development)

## Getting Started

1. Clone the repository:
   ```
   git clone https://github.com/jsydorowicz21/aws-ts-starter.git
   cd aws-ts-starter
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Set up your AWS credentials if you haven't already:
   ```
   aws configure
   ```

## Local Development

To run the application locally with Docker:
```
npm run deploy:docker
```

To run the application locally without Docker:

```
npm run start
```
> **Alert:** Running the application locally with `npm run start` requires a separate DynamoDB instance to be running. If you don't have a local DynamoDB instance set up, it's recommended to use the Docker method described above (`npm run deploy:docker`), which includes a local DynamoDB instance.

## Deployment

To deploy the application to AWS:
```
npm run deploy
```
This command uses Serverless Framework to deploy your application to AWS, creating all necessary resources including Lambda functions, API Gateway, and DynamoDB table.

## Tear Down

To remove the application after deployment:
```
npm run remove
```
This command removes all AWS resources created by the Serverless Framework.

## Available Scripts

- `start`: Runs the application locally using Serverless Offline
- `deploy`: Deploys the application to AWS
- `deploy:docker`: Builds and runs the application in a Docker container
- `remove`: Removes the application from AWS
- `test`: Runs Jest tests

## Project Structure

- `src/handlers/`: Contains Lambda function handlers
- `src/services/`: Services for interacting with DynamoDB
- `src/models/`: TypeScript interfaces and types
- `tests/`: Jest test files
- `serverless.ts`: Serverless Framework configuration

## API Endpoints

The application provides the following API endpoints:

- POST /items: Create a new item
- GET /items/{id}: Retrieve a specific item
- PUT /items/{id}: Update an existing item
- DELETE /items/{id}: Delete an item
- GET /items: List all items

## Configuration

The main configuration for the Serverless Framework is in `serverless.ts`. Key configurations include:

- `stage`: The stage name (e.g., `dev`, `prod`)
- `region`: The AWS region (e.g., `us-east-1`)
- `dynamodb`: DynamoDB configuration
- `api`: API Gateway configuration
- `functions`: Lambda function configurations

## Testing

To run the tests:
```
npm run test
```
