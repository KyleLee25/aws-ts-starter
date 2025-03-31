import React, { useState, useEffect } from 'react';
import { itemsApi, Item } from '../api/itemsApi';
import './Items.css';

export function Items() {
  const [items, setItems] = useState<Item[]>([]);
  const [newItem, setNewItem] = useState<Item>({
    name: '',
    description: '',
    price: 0
  });
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');

  useEffect(() => {
    loadItems();
  }, []);

  const loadItems = async () => {
    try {
      const data = await itemsApi.listItems();
      setItems(data);
    } catch (err) {
      setError('Error loading items');
    }
  };

  const handleCreateItem = async () => {
    try {
      await itemsApi.createItem(newItem);
      setNewItem({ name: '', description: '', price: 0 });
      setSuccess('Item created successfully');
      loadItems();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Error creating item');
      setTimeout(() => setError(''), 3000);
    }
  };

  const handleDeleteItem = async (id: string) => {
    try {
      await itemsApi.deleteItem(id);
      setSuccess('Item deleted successfully');
      loadItems();
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Error deleting item');
      setTimeout(() => setError(''), 3000);
    }
  };

  return (
    <div className="container">
      <h1>Items Manager</h1>

      {error && <div className="error-message">{error}</div>}
      {success && <div className="success-message">{success}</div>}

      <div className="form-container">
        <input
          type="text"
          placeholder="Item name"
          value={newItem.name}
          onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
        />
        <textarea
          placeholder="Item description"
          value={newItem.description}
          onChange={(e) => setNewItem({ ...newItem, description: e.target.value })}
        />
        <input
          type="number"
          placeholder="Price"
          value={newItem.price}
          onChange={(e) => setNewItem({ ...newItem, price: parseFloat(e.target.value) })}
        />
        <button onClick={handleCreateItem}>Create Item</button>
      </div>

      <div className="items-list">
        {items.map((item) => (
          <div key={item.id} className="item-card">
            <div className="item-content">
              <h3>{item.name}</h3>
              <p>{item.description}</p>
              <p className="price">${item.price}</p>
              {item.createdAt && (
                <p className="date">Created: {new Date(item.createdAt).toLocaleDateString()}</p>
              )}
            </div>
            <button 
              className="delete-button"
              onClick={() => item.id && handleDeleteItem(item.id)}
            >
              Delete
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}