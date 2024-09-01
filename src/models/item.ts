export interface Item {
    id: string;
    name: string;
    price: number;
    description: string;
    createdAt: string;
    updatedAt: string;
  }
  
  export interface CreateItemInput {
    id: string;
    name: string;
    price: number;
    description: string;
  }
  
  export interface UpdateItemInput {
    id: string;
    name?: string;
    price?: number;
    description?: string;
  }