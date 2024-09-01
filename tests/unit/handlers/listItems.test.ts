import { handler } from "../../../src/handlers/listItems";
import { APIGatewayProxyEvent, APIGatewayProxyResult, Context, Callback } from "aws-lambda";
import db from "../../../src/services/dynamoDB";

jest.mock("../../../src/services/dynamoDB");
    
describe("listItems", () => {
    it("should list items", async () => {
        const mockItems = [{
            id: "123",
            name: "Test Item",
            price: 10,
            description: "This is a test item",
            createdAt: "2024-02-14T12:00:00Z",
            updatedAt: "2024-02-14T12:00:00Z"
        }];

        (db.listItems as jest.Mock).mockResolvedValue(mockItems);


        const result = await handler({} as APIGatewayProxyEvent, {} as Context, {} as Callback) as APIGatewayProxyResult;

        expect(result.statusCode).toBe(200);
        expect(JSON.parse(result.body)).toEqual(mockItems);
        expect(db.listItems).toHaveBeenCalled();
    });

    it("should return 404 for no items", async () => {
        const event: APIGatewayProxyEvent = {
            pathParameters: null,
        } as unknown as APIGatewayProxyEvent;

        (db.listItems as jest.Mock).mockResolvedValue(null);
        const result = await handler(event, {} as Context, {} as Callback) as APIGatewayProxyResult;

        expect(result.statusCode).toBe(404);
        expect(JSON.parse(result.body)).toEqual({ message: "Items not found" });
    });
});