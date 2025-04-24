import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import studentApi from '../../api/studentApi';
import './Auth.css';

const SignUp: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    major: '',
    enrollmentYear: new Date().getFullYear(),
    graduationYear: new Date().getFullYear() + 4,
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'enrollmentYear' || name === 'graduationYear' 
        ? parseInt(value) || 0 
        : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Ensure all fields are properly formatted
      const studentData = {
        name: formData.name.trim(),
        email: formData.email.trim(),
        major: formData.major.trim(),
        enrollmentYear: Number(formData.enrollmentYear),
        graduationYear: Number(formData.graduationYear)
      };

      console.log('Attempting to create student:', studentData);
      
      const studentResponse = await studentApi.createStudent(studentData);
      console.log('Student created successfully:', studentResponse);
      
      navigate('/login');
    } catch (err) {
      console.error('Signup error:', err);
      
      if (err instanceof Error) {
        setError(err.message);
      } else if (typeof err === 'object' && err !== null) {
        if ('response' in err && err.response) {
          const response = err.response as any;
          if (response.data && response.data.message) {
            setError(response.data.message);
          } else if (response.status === 409) {
            setError('A student with this email already exists');
          } else if (response.status === 400) {
            setError('Invalid input data. Please check your information and try again');
          } else {
            setError('An error occurred during signup');
          }
        } else if ('message' in err) {
          setError(String(err.message));
        } else {
          setError('An unexpected error occurred during signup');
        }
      } else {
        setError('An unexpected error occurred during signup');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Student Portal Sign Up</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="name">Full Name</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength={8}
            />
          </div>
          <div className="form-group">
            <label htmlFor="major">Major</label>
            <input
              type="text"
              id="major"
              name="major"
              value={formData.major}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="enrollmentYear">Enrollment Year</label>
            <input
              type="number"
              id="enrollmentYear"
              name="enrollmentYear"
              value={formData.enrollmentYear}
              onChange={handleChange}
              required
              min={2000}
              max={new Date().getFullYear()}
            />
          </div>
          <div className="form-group">
            <label htmlFor="graduationYear">Expected Graduation Year</label>
            <input
              type="number"
              id="graduationYear"
              name="graduationYear"
              value={formData.graduationYear}
              onChange={handleChange}
              required
              min={formData.enrollmentYear}
              max={formData.enrollmentYear + 6}
            />
          </div>
          <button 
            type="submit" 
            className="login-button"
            disabled={loading}
          >
            {loading ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>
        <div className="auth-footer">
          Already have an account?{' '}
          <button 
            className="link-button"
            onClick={() => navigate('/login')}
          >
            Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignUp;  