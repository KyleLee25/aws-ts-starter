import { DynamoDBClient, PutItemCommand, GetItemCommand, ScanCommand, DeleteItemCommand, PutItemCommandOutput, GetItemCommandOutput, ScanCommandOutput, DeleteItemCommandOutput } from "@aws-sdk/client-dynamodb";
import { marshall, unmarshall } from "@aws-sdk/util-dynamodb";
import { UpdateItemInput, CreateItemInput, Item } from "../models/item";

const isLocal = process.env.IS_OFFLINE === 'true' || process.env.NODE_ENV === 'development';

const dynamoDbClientParams = {
  region: isLocal ? "localhost" : (process.env.AWS_REGION || "us-east-1"),
  ...(isLocal && {
    endpoint: process.env.DYNAMODB_ENDPOINT || "http://dynamodb:8000",
    credentials: {
      accessKeyId: process.env.AWS_ACCESS_KEY_ID || "DUMMYIDEXAMPLE",
      secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY || "DUMMYEXAMPLEKEY"
    }
  })
};

const client = new DynamoDBClient(dynamoDbClientParams);

export const TABLE_NAME = process.env.TABLE_NAME;

const db = {
    createItem: async (itemInput: CreateItemInput) => {
        try {
            const command = new PutItemCommand({
                TableName: TABLE_NAME,
                Item: marshall(itemInput),
            });

            const response: PutItemCommandOutput = await client.send(command);
            if (response.$metadata.httpStatusCode === 200) {
                return itemInput as Item;
            }

            return null;
        } catch (error) {
            console.error("Error creating item:", error);
            throw error;
        }
    },
    getItem: async (id: string) => {
        try {
            const command = new GetItemCommand({
                TableName: TABLE_NAME,
                Key: marshall({ id }),
            });
            
            const response: GetItemCommandOutput = await client.send(command);
            return response.Item ? unmarshall(response.Item) as Item : null;
        } catch (error) {
            console.error("Error getting item:", error);
            throw error;
        }
    },
    listItems: async () => {
        try {
            const command = new ScanCommand({
                TableName: TABLE_NAME,
            });
            
            const response: ScanCommandOutput = await client.send(command);
            return response.Items?.map((item) => unmarshall(item)) as Item[];
        } catch (error) {
            console.error("Error listing items:", error);
            throw error;
        }
    },
    updateItem: async (id: string, input: UpdateItemInput) => {
        try {
            const command = new PutItemCommand({
                TableName: TABLE_NAME,
                Item: marshall({ id, input }),
            });

            const response: PutItemCommandOutput = await client.send(command);
            if (response.$metadata.httpStatusCode === 200) {
                input.id = id;
                return input;
            }

            return null;
        } catch (error) {
            console.error("Error updating item:", error);
            throw error;
        }
    },
    deleteItem: async (id: string) => {
        try {
            const command = new DeleteItemCommand({
            TableName: TABLE_NAME,
                Key: marshall({ id }),
            });

            const response: DeleteItemCommandOutput = await client.send(command);
            if (response.$metadata.httpStatusCode === 200) {
                return { id };
            }

            return null;
        } catch (error) {
            console.error("Error deleting item:", error);
            throw error;
        }
    }
}

export default db;
