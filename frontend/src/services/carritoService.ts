import api from './api';
import type {
  Carrito,
  AddItemCarritoRequest,
  UpdateItemCarritoRequest,
  ResponseDTO
} from '../types';

export const carritoService = {

  async getCarrito(): Promise<Carrito> {
    const response = await api.get<ResponseDTO<Carrito>>('/carrito');
    return response.data.data;
  },


  async addItem(item: AddItemCarritoRequest): Promise<Carrito> {
    const response = await api.post<ResponseDTO<Carrito>>('/carrito/items', item);
    return response.data.data;
  },


  async updateItemQuantity(itemId: number, cantidad: number): Promise<Carrito> {
    const request: UpdateItemCarritoRequest = { cantidad };
    const response = await api.put<ResponseDTO<Carrito>>(
      `/carrito/items/${itemId}`,
      request
    );
    return response.data.data;
  },


  async removeItem(itemId: number): Promise<void> {
    await api.delete<ResponseDTO<null>>(`/carrito/items/${itemId}`);
  },
};
