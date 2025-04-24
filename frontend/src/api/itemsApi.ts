import axios from 'axios';

const API_BASE_URL = 'https://n5qc9fw709.execute-api.us-east-1.amazonaws.com/prod';

export interface Item {
  id?: string;
  name: string;
  description: string;
  price: number;
  createdAt?: string;
  updatedAt?: string;
}

export const itemsApi = {
  listItems: async () => {
    const response = await axios.get(`${API_BASE_URL}/items`);
    return response.data;
  },

  createItem: async (item: Item) => {
    const response = await axios.post(`${API_BASE_URL}/items`, item);
    return response.data;
  },

  deleteItem: async (id: string) => {
    const response = await axios.delete(`${API_BASE_URL}/items/${id}`);
    return response.data;
  }
};