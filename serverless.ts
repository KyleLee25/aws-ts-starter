import type { AWS } from "@serverless/typescript";

const serverlessConfiguration: AWS = {
    service: "aws-ts-starter",
    frameworkVersion: "3",
    provider: {
        name: "aws",
        runtime: "nodejs18.x",
        region: "us-east-1",
        apiGateway: {
            minimumCompressionSize: 1024,
            shouldStartNameWithService: true,
        },
        environment: {
            AWS_NODEJS_CONNECTION_REUSE_ENABLED: "1",
            NODE_OPTIONS: "--enable-source-maps --stack-trace-limit=1000",
            TABLE_NAME: "${self:service}-${opt:stage, self:provider.stage}",
        },
        iam: {
            role: {
              statements: [
                {
                  Effect: "Allow",
                  Action: [
                    "dynamodb:PutItem",
                    "dynamodb:GetItem",
                    "dynamodb:UpdateItem",
                    "dynamodb:DeleteItem",
                    "dynamodb:Scan",
                    "dynamodb:Query"
                  ],
                  Resource: "arn:aws:dynamodb:${self:provider.region}:*:table/${self:provider.environment.TABLE_NAME}"
                }
              ]
            }
        },
    },
    resources: {
        Resources: {
            ItemsTable: {
              Type: "AWS::DynamoDB::Table",
              Properties: {
                TableName: "${self:provider.environment.TABLE_NAME}",
                AttributeDefinitions: [
                  {
                    AttributeName: "id",
                    AttributeType: "S",
                  },
                ],
                KeySchema: [
                  {
                    AttributeName: "id",
                    KeyType: "HASH",
                  },
                ],
                BillingMode: "PAY_PER_REQUEST",
              },
            },
        },
    },
    functions: {
        createItem: {
            handler: "src/handlers/createItem.handler",
            events: [
                {
                    http: {
                        method: "post",
                        path: "/items",
                        cors: {
                            origin: '*',
                            headers: [
                                'Content-Type',
                                'X-Amz-Date',
                                'Authorization',
                                'X-Api-Key',
                                'X-Amz-Security-Token',
                                'X-Amz-User-Agent'
                            ],
                            allowCredentials: false
                        }
                    }
                }
            ]
        },
        getItem: {
            handler: "src/handlers/getItem.handler",
            events: [
                {
                    http: {
                        method: "get",
                        path: "/items/{id}",
                        cors: {
                            origin: '*',
                            headers: [
                                'Content-Type',
                                'X-Amz-Date',
                                'Authorization',
                                'X-Api-Key',
                                'X-Amz-Security-Token',
                                'X-Amz-User-Agent'
                            ],
                            allowCredentials: false
                        }
                    }
                }
            ]
        },
        updateItem: {
            handler: "src/handlers/updateItem.handler",
            events: [
                {
                    http: {
                        method: "put",
                        path: "/items/{id}",
                        cors: {
                            origin: '*',
                            headers: [
                                'Content-Type',
                                'X-Amz-Date',
                                'Authorization',
                                'X-Api-Key',
                                'X-Amz-Security-Token',
                                'X-Amz-User-Agent'
                            ],
                            allowCredentials: false
                        }
                    }
                }
            ]
        },
        deleteItem: {
            handler: "src/handlers/deleteItem.handler",
            events: [
                {
                    http: {
                        method: "delete",
                        path: "/items/{id}",
                        cors: {
                            origin: '*',
                            headers: [
                                'Content-Type',
                                'X-Amz-Date',
                                'Authorization',
                                'X-Api-Key',
                                'X-Amz-Security-Token',
                                'X-Amz-User-Agent'
                            ],
                            allowCredentials: false
                        }
                    }
                }
            ]
        },
        listItems: {
            handler: "src/handlers/listItems.handler",
            events: [
                {
                    http: {
                        method: "get",
                        path: "/items",
                        cors: {
                            origin: '*',
                            headers: [
                                'Content-Type',
                                'X-Amz-Date',
                                'Authorization',
                                'X-Api-Key',
                                'X-Amz-Security-Token',
                                'X-Amz-User-Agent'
                            ],
                            allowCredentials: false
                        }
                    }
                }
            ]
        }
    },
    package: {
        individually: true,
        exclude: ["node_modules/**", "README.md", "LICENSE", "package.json"],
    },
    custom: {
        esbuild: {
            bundle: true,
            minify: false,
            sourcemap: true,
            exclude: ['aws-sdk'],
            target: 'node14',
            define: { 'require.resolve': undefined },
            platform: 'node',
            concurrency: 10,
        },
    },
    plugins: ['serverless-esbuild', 'serverless-offline'],
}

module.exports = serverlessConfiguration;