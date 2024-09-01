import { APIGatewayProxyHandler } from "aws-lambda";
import db from "../services/dynamoDB";

export const handler: APIGatewayProxyHandler = async (event, context) => {
    if (!event.pathParameters) {
        return {
            statusCode: 400,
            body: JSON.stringify({ message: "Invalid request parameters" }),
        };
    }

    const { id } = event.pathParameters as { id: string };
    const result = await db.deleteItem(id);

    if (!result) {
        return {
            statusCode: 500,
            body: JSON.stringify({ message: "Failed to delete item" }),
        };
    }

    return {
        statusCode: 200,
        body: JSON.stringify(result),
    }
}