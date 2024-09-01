import { APIGatewayProxyHandler } from "aws-lambda";
import db from "../services/dynamoDB";
import { Item } from "../models/item";

export const handler: APIGatewayProxyHandler = async (event, context) => {
    if (!event.pathParameters) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Invalid request parameters" }),
        };
    }

    const { id } = event.pathParameters as { id: string };
    const item: Item | null = await db.getItem(id) as Item | null;

    if (!item) {
        return {
            statusCode: 404,
            body: JSON.stringify({ message: "Item not found" }),
        };
    }
    
    return {
        statusCode: 200,
        body: JSON.stringify(item),
    };
}
