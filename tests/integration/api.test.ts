import axios from 'axios';


let API_URL = 'http://0.0.0.0:3000/dev';


const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

describe('API Integration Tests', () => {
  let createdItemId: string;

  afterEach(async () => {
    await delay(1000); // Add a small delay between tests
  });

  test('Create Item', async () => {
    try {
      const response = await axios.post(`${API_URL}/items`, {
        name: 'Test Item',
        price: 10,
        description: 'This is a test item'
      });

      expect(response.status).toBe(201);
      expect(response.data).toHaveProperty('id');
      
      createdItemId = response.data.id;
    } catch (error) {
      console.error('Create Item Error:', error);
      throw error;
    }
  });

  test('Get Item', async () => {
    const response = await axios.get(`${API_URL}/items/${createdItemId}`);

    expect(response.status).toBe(200);
    expect(response.data).toHaveProperty('id', createdItemId);
  });

  test('Update Item', async () => {
    const response = await axios.put(`${API_URL}/items/${createdItemId}`, {
      name: 'Updated Test Item',
      price: 15,
      description: 'This is an updated test item'
    });

    expect(response.status).toBe(200);
    expect(response.data).toHaveProperty('name', 'Updated Test Item');
  });

  test('List Items', async () => {
    const response = await axios.get(`${API_URL}/items`);

    expect(response.status).toBe(200);
    expect(Array.isArray(response.data)).toBe(true);
    expect(response.data.length).toBeGreaterThan(0);
  });

  test('Delete Item', async () => {
    const response = await axios.delete(`${API_URL}/items/${createdItemId}`);

    expect(response.status).toBe(200);
  });

  afterAll(async () => {
    await delay(2000); // Add a delay after all tests
  });
});