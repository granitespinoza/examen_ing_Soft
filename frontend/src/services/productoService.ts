import api from './api';
import type { Producto, CreateProductoRequest, UpdateProductoRequest, ResponseDTO } from '../types';

export const productoService = {

  async getAll(): Promise<Producto[]> {
    const response = await api.get<ResponseDTO<Producto[]>>('/productos');
    return response.data.data;
  },

  async getById(id: number): Promise<Producto> {
    const response = await api.get<ResponseDTO<Producto>>(`/productos/${id}`);
    return response.data.data;
  },


  async create(producto: CreateProductoRequest): Promise<Producto> {
    const response = await api.post<ResponseDTO<Producto>>('/productos', producto);
    return response.data.data;
  },

  async update(id: number, producto: UpdateProductoRequest): Promise<Producto> {
    const response = await api.put<ResponseDTO<Producto>>(`/productos/${id}`, producto);
    return response.data.data;
  },


  async delete(id: number): Promise<void> {
    await api.delete<ResponseDTO<null>>(`/productos/${id}`);
  },
};
