import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        const token = localStorage.getItem('token');
        if (storedUser && token) {
            setUser(JSON.parse(storedUser));
        }
        setLoading(false);
    }, []);

    const login = async (email, password) => {
        try {
            const response = await authService.login({ email, password });
            const { accessToken, refreshToken, ...userData } = response.data;

            // We need to fetch user details if login only returns tokens.
            // But looking at AuthService.login, it returns TokenResponse { accessToken, refreshToken }.
            // It DOES NOT return user info.
            // We might need an endpoint to get 'me' or decode the token.
            // For now, let's assume we decode token or store email.
            // Wait, AuthService.login returns TokenResponse. 
            // I should probably decode the JWT to get role/email or add an endpoint 'me'.
            // For simplicity in this iteration, I'll store the token and email (from input).

            // Actually, let's decode the token to get role/sub if possible, or just use what we have.
            // Let's store basic info.

            localStorage.setItem('token', accessToken);
            localStorage.setItem('refreshToken', refreshToken);

            // Ideally fetch user profile here.
            // I will assume the email is the user identifier.
            const userObj = { email };
            setUser(userObj);
            localStorage.setItem('user', JSON.stringify(userObj));

            return response.data;
        } catch (error) {
            throw error;
        }
    };

    const register = async (data) => {
        try {
            const response = await authService.register(data);
            // Register returns AuthResponse which has token, userId, email, role.
            // This is better.
            const { token, ...userData } = response.data;
            localStorage.setItem('token', token);
            setUser(userData);
            localStorage.setItem('user', JSON.stringify(userData));
            return response.data;
        } catch (error) {
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
