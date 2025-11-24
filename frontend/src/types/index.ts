export type TipoUsuario = 'CLIENTE' | 'COMERCIANTE';

export type CategoriaProducto =
  | 'ELECTRONICA'
  | 'ROPA'
  | 'ALIMENTOS'
  | 'HOGAR'
  | 'DEPORTES'
  | 'JUGUETES'
  | 'LIBROS'
  | 'SALUD_BELLEZA'
  | 'OTROS';

export type EstadoOrden =
  | 'PENDIENTE'
  | 'PROCESANDO'
  | 'ENVIADO'
  | 'ENTREGADO'
  | 'CANCELADO';

export interface ResponseDTO<T> {
  success: boolean;
  message?: string;
  data: T;
}

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  tipoUsuario: TipoUsuario;
  fechaRegistro?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegistroRequest {
  nombre: string;
  email: string;
  password: string;
  tipoUsuario: TipoUsuario;
  cif?: string;
  nombreNegocio?: string;
  direccion?: string;
  descripcion?: string;
  phoneNumber?: string;
}

export interface AuthResponse {
  token: string;
  usuario: Usuario;
}

export interface Producto {
  id?: number;
  comercianteId?: number;
  nombreComercio?: string;
  nombre: string;
  descripcion?: string;
  categoria: CategoriaProducto;
  precio: number;
  stock: number;
  fechaCreacion?: string;
}

export interface CreateProductoRequest {
  nombre: string;
  descripcion?: string;
  categoria: CategoriaProducto;
  precio: number;
  stock: number;
}

export interface UpdateProductoRequest {
  nombre?: string;
  descripcion?: string;
  categoria?: CategoriaProducto;
  precio?: number;
  stock?: number;
}

export interface ItemCarrito {
  id?: number;
  productoId: number;
  productoNombre?: string;
  cantidad: number;
  precioUnitario?: number;
  total?: number;
}

export interface Carrito {
  id?: number;
  clienteId?: number;
  items: ItemCarrito[];
  total?: number;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export interface AddItemCarritoRequest {
  productoId: number;
  cantidad: number;
}

export interface UpdateItemCarritoRequest {
  cantidad: number;
}

export interface ItemOrden {
  id?: number;
  productoId: number;
  productoNombre?: string;
  cantidad: number;
  precioUnitario?: number;
  subTotal?: number;
}

export interface Orden {
  id?: number;
  clienteId?: number;
  items: ItemOrden[];
  fechaOrden?: string;
  estado?: EstadoOrden;
  total?: number;
  direccionEnvio: string;
}

export interface CreateOrdenRequest {
  direccionEnvio: string;
}

export interface UpdateEstadoOrdenRequest {
  estado: EstadoOrden;
}
