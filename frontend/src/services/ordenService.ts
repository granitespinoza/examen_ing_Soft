import api from './api';
import type {
  Orden,
  CreateOrdenRequest,
  UpdateEstadoOrdenRequest,
  ResponseDTO
} from '../types';

export const ordenService = {

  async createOrden(direccionEnvio: string): Promise<Orden> {
    const request: CreateOrdenRequest = { direccionEnvio };
    const response = await api.post<ResponseDTO<Orden>>('/ordenes', request);
    return response.data.data;
  },


  async getMisOrdenes(): Promise<Orden[]> {
    const response = await api.get<ResponseDTO<Orden[]>>('/ordenes');
    return response.data.data;
  },


  async getOrdenById(id: number): Promise<Orden> {
    const response = await api.get<ResponseDTO<Orden>>(`/ordenes/${id}`);
    return response.data.data;
  },


  async updateEstado(id: number, estado: UpdateEstadoOrdenRequest): Promise<Orden> {
    const response = await api.patch<ResponseDTO<Orden>>(
      `/ordenes/${id}/estado`,
      estado
    );
    return response.data.data;
  },
};
