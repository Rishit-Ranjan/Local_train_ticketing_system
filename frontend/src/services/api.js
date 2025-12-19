import axios from 'axios';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || '/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add a request interceptor to add the auth token to headers
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add a response interceptor to handle token expiration (optional basic implementation)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 403) {
            // Token invalid or expired
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            // Window reload or redirect could happen here, but better handled in Context
        }
        return Promise.reject(error);
    }
);

export const authService = {
    login: (credentials) => api.post('/auth/login', credentials),
    register: (data) => api.post('/auth/register', data),
    refreshToken: (token) => api.post('/auth/refresh-token', { token }),
};

export const bookingService = {
    createBooking: (data) => api.post('/bookings', data),
    getUserBookings: () => api.get('/bookings/my-bookings'),
    cancelBooking: (id) => api.post(`/bookings/${id}/cancel`),
    searchSchedules: (sourceId, destinationId, date) =>
        api.get(`/bookings/search?sourceId=${sourceId}&destinationId=${destinationId}&date=${date}`),
    getBookedSeats: (scheduleId) => api.get(`/bookings/schedules/${scheduleId}/seats`),
    getAllStations: () => api.get('/bookings/stations'),
    downloadTicket: (id) => api.get(`/bookings/${id}/ticket`, { responseType: 'blob' }),
};

export const walletService = {
    getWallet: () => api.get('/wallet'),
    addFunds: (amount, paymentMethod) => api.post('/wallet/add', { amount, paymentMethod }),
    getTransactions: () => api.get('/wallet/transactions'),
};

export const adminService = {
    createStation: (data) => api.post('/admin/stations', data),
    getAllStations: () => api.get('/admin/stations'),
    createSchedule: (data) => api.post('/admin/schedules', data),
};

export default api;
