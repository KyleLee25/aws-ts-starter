import { APIGatewayProxyHandler } from "aws-lambda";
import { v4 as uuid } from "uuid";
import db from "../services/dynamoDB";
import { CreateItemInput, Item } from "../models/item";

export const handler: APIGatewayProxyHandler = async (event, context) => {
    if (!event.body) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Invalid request body" }),
        };
    }

    const id = uuid();
    const { name, price, description } = JSON.parse(event.body);
    const newItem : CreateItemInput = {
        id,
        name,
        price,
        description,
    }
    const itemReturn: Item | null = await db.createItem(newItem);

    if (!itemReturn) {
        return {
            statusCode: 500,
            body: JSON.stringify({ message: "Failed to create item" }),
        };
    }

    return {
        statusCode: 201,
        body: JSON.stringify(itemReturn),
    }
}

