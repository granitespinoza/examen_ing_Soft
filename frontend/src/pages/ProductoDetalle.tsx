import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productoService } from '../services/productoService';
import { carritoService } from '../services/carritoService';
import { useAuthStore } from '../store/useAuthStore';
import { useState } from 'react';

export default function ProductoDetalle() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user, isAuthenticated } = useAuthStore();
  const [cantidad, setCantidad] = useState(1);
  const [mensaje, setMensaje] = useState('');

  const { data: producto, isLoading, error } = useQuery({
    queryKey: ['producto', id],
    queryFn: () => productoService.getById(Number(id)),
    enabled: !!id,
  });

  const addToCartMutation = useMutation({
    mutationFn: (cantidad: number) =>
      carritoService.addItem({ productoId: Number(id), cantidad }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['carrito'] });
      setMensaje('Producto agregado al carrito');
      setTimeout(() => setMensaje(''), 3000);
    },
    onError: (error: any) => {
      setMensaje(error.response?.data?.message || 'Error al agregar al carrito');
      setTimeout(() => setMensaje(''), 3000);
    },
  });

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (user?.tipoUsuario === 'COMERCIANTE') {
      setMensaje('Los comerciantes no pueden agregar productos al carrito');
      setTimeout(() => setMensaje(''), 3000);
      return;
    }
    addToCartMutation.mutate(cantidad);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <div className="text-xl">Cargando producto...</div>
      </div>
    );
  }

  if (error || !producto) {
    return (
      <div className="max-w-4xl mx-auto p-6">
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          Producto no encontrado
        </div>
        <button
          onClick={() => navigate('/productos')}
          className="mt-4 px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
        >
          Volver a productos
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <button
        onClick={() => navigate('/productos')}
        className="mb-6 px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
      >
        ← Volver a productos
      </button>

      {mensaje && (
        <div className="mb-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded">
          {mensaje}
        </div>
      )}

      <div className="grid md:grid-cols-2 gap-8">
        {/* Imagen del producto */}
        <div className="bg-gray-100 rounded-lg p-8 flex items-center justify-center">
          <div className="text-gray-400 text-6xl">📦</div>
        </div>

        {/* Información del producto */}
        <div>
          <h1 className="text-3xl font-bold mb-4">{producto.nombre}</h1>

          {producto.nombreComercio && (
            <p className="text-gray-600 mb-2">
              <span className="font-semibold">Vendido por:</span> {producto.nombreComercio}
            </p>
          )}

          <div className="mb-4">
            <span className="inline-block bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm">
              {producto.categoria}
            </span>
          </div>

          {producto.descripcion && (
            <div className="mb-6">
              <h2 className="text-xl font-semibold mb-2">Descripción</h2>
              <p className="text-gray-700">{producto.descripcion}</p>
            </div>
          )}

          <div className="mb-6">
            <p className="text-3xl font-bold text-green-600">
              ${producto.precio.toFixed(2)}
            </p>
          </div>

          <div className="mb-6">
            <p className="text-gray-700">
              <span className="font-semibold">Stock disponible:</span>{' '}
              {producto.stock > 0 ? (
                <span className="text-green-600">{producto.stock} unidades</span>
              ) : (
                <span className="text-red-600">Sin stock</span>
              )}
            </p>
          </div>

          {producto.stock > 0 && user?.tipoUsuario !== 'COMERCIANTE' && (
            <div className="space-y-4">
              <div>
                <label htmlFor="cantidad" className="block text-sm font-semibold mb-2">
                  Cantidad:
                </label>
                <input
                  id="cantidad"
                  type="number"
                  min="1"
                  max={producto.stock}
                  value={cantidad}
                  onChange={(e) => setCantidad(Math.max(1, Math.min(producto.stock, Number(e.target.value))))}
                  className="w-24 px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <button
                onClick={handleAddToCart}
                disabled={addToCartMutation.isPending}
                className="w-full px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed font-semibold"
              >
                {addToCartMutation.isPending ? 'Agregando...' : 'Agregar al Carrito'}
              </button>
            </div>
          )}

          {producto.stock === 0 && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
              Producto agotado
            </div>
          )}

          {producto.fechaCreacion && (
            <p className="text-sm text-gray-500 mt-6">
              Publicado el {new Date(producto.fechaCreacion).toLocaleDateString('es-ES')}
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
