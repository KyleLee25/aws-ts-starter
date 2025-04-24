import axios from 'axios';

const API_BASE_URL ='https://n5qc9fw709.execute-api.us-east-1.amazonaws.com/prod';

export interface Student {
  id: string;
  name: string;
  email: string;
  major: string;
  enrollmentYear: number;
  graduationYear: number;
  createdAt: string;
  updatedAt: string;
}

export interface Course {
  id: string;
  code: string;
  name: string;
  description: string;
  credits: number;
}

export interface Grade {
  id: string;
  courseId: string;
  studentId: string;
  grade: string;
  semester: string;
}

const studentApi = {
  // Student Profile
  createStudent: async (studentData: Omit<Student, 'id' | 'createdAt' | 'updatedAt'>) => {
    try {
      console.log('Sending student data to API:', studentData);
      const response = await axios.post(`${API_BASE_URL}/students`, studentData, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
      console.log('API response:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('API error details:', {
        message: error?.message || 'Unknown error',
        response: error?.response?.data || 'No response data',
        status: error?.response?.status || 'No status code'
      });
      throw error;
    }
  },

  getProfile: async (studentId: string) => {
    const response = await axios.get(`${API_BASE_URL}/students/${studentId}`);
    return response.data;
  },

  updateProfile: async (studentId: string, data: Partial<Student>) => {
    const response = await axios.put(`${API_BASE_URL}/students/${studentId}`, data);
    return response.data;
  },

  // Courses
  getCourses: async () => {
    const response = await axios.get(`${API_BASE_URL}/courses`);
    return response.data;
  },

  registerCourse: async (studentId: string, courseId: string) => {
    const response = await axios.post(`${API_BASE_URL}/students/${studentId}/courses`, {
      courseId,
    });
    return response.data;
  },

  // Grades
  getGrades: async (studentId: string) => {
    const response = await axios.get(`${API_BASE_URL}/students/${studentId}/grades`);
    return response.data;
  },

  // Notifications
  getNotifications: async (studentId: string) => {
    const response = await axios.get(`${API_BASE_URL}/students/${studentId}/notifications`);
    return response.data;
  },
};

export default studentApi; 