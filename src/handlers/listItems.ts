import { APIGatewayProxyHandler } from "aws-lambda";
import db from "../services/dynamoDB";
import { Item } from "../models/item";

export const handler: APIGatewayProxyHandler = async (event, context) => {
    const items: Item[] = await db.listItems();

    if (!items) {
        return {
            statusCode: 404,
            body: JSON.stringify({ message: "Items not found" }),
        };
    }
    
    return {
        statusCode: 200,
        body: JSON.stringify(items),
    }
}