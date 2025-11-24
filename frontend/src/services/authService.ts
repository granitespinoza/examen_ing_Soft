import api from './api';
import type { LoginRequest, RegistroRequest, AuthResponse, ResponseDTO, Usuario } from '../types';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<ResponseDTO<AuthResponse>>('/auth/login', credentials);
    const authData = response.data.data;

    if (authData.token) {
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData.usuario));
    }
    return authData;
  },

  async register(data: RegistroRequest): Promise<AuthResponse> {
    const response = await api.post<ResponseDTO<AuthResponse>>('/auth/register', data);
    const authData = response.data.data;

    if (authData.token) {
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData.usuario));
    }
    return authData;
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getCurrentUser(): Usuario | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  },
};
