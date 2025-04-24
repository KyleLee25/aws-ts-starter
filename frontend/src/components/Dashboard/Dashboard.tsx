import React, { useState } from 'react';
import { Link, Outlet } from 'react-router-dom';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const [notifications, setNotifications] = useState([
    { id: 1, message: 'Registration for Spring 2024 opens next week', date: '2024-04-20' },
    { id: 2, message: 'Your grade for CS101 has been posted', date: '2024-04-19' },
  ]);

  return (
    <div className="dashboard-container">
      <nav className="sidebar">
        <div className="sidebar-header">
          <h2>Student Portal</h2>
        </div>
        <ul className="nav-links">
          <li>
            <Link to="/dashboard/overview">Overview</Link>
          </li>
          <li>
            <Link to="/dashboard/courses">Courses</Link>
          </li>
          <li>
            <Link to="/dashboard/grades">Grades</Link>
          </li>
          <li>
            <Link to="/dashboard/profile">Profile</Link>
          </li>
        </ul>
      </nav>
      <main className="main-content">
        <header className="top-bar">
          <div className="notifications">
            <h3>Notifications</h3>
            <div className="notification-list">
              {notifications.map((notification) => (
                <div key={notification.id} className="notification-item">
                  <p>{notification.message}</p>
                  <span className="notification-date">{notification.date}</span>
                </div>
              ))}
            </div>
          </div>
        </header>
        <div className="content-area">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default Dashboard; 