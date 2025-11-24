import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { carritoService } from '../services/carritoService';
import { ordenService } from '../services/ordenService';

function Carrito() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [direccionEnvio, setDireccionEnvio] = useState('');
  const [showCheckout, setShowCheckout] = useState(false);

  const { data: carrito, isLoading } = useQuery({
    queryKey: ['carrito'],
    queryFn: carritoService.getCarrito,
  });

  const updateQuantityMutation = useMutation({
    mutationFn: ({ itemId, cantidad }: { itemId: number; cantidad: number }) =>
      carritoService.updateItemQuantity(itemId, cantidad),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['carrito'] });
    },
  });

  const removeItemMutation = useMutation({
    mutationFn: carritoService.removeItem,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['carrito'] });
    },
  });

  const createOrdenMutation = useMutation({
    mutationFn: (direccion: string) => ordenService.createOrden(direccion),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['carrito'] });
      alert('Orden creada exitosamente!');
      navigate('/ordenes');
    },
  });

  const handleUpdateQuantity = (itemId: number, cantidad: number) => {
    if (cantidad < 1) return;
    updateQuantityMutation.mutate({ itemId, cantidad });
  };

  const handleRemoveItem = (itemId: number) => {
    if (window.confirm('¿Eliminar este producto del carrito?')) {
      removeItemMutation.mutate(itemId);
    }
  };

  const handleCheckout = () => {
    if (!direccionEnvio.trim()) {
      alert('Por favor ingresa una dirección de envío');
      return;
    }
    createOrdenMutation.mutate(direccionEnvio);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Cargando carrito...</div>
      </div>
    );
  }

  const total = carrito?.items?.reduce((sum, item) => sum + (item.total || 0), 0) || 0;
  const isEmpty = !carrito?.items || carrito.items.length === 0;

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-2">Mi Carrito</h1>
        <p className="text-gray-600">
          {isEmpty ? 'Tu carrito está vacío' : `${carrito.items.length} producto(s) en el carrito`}
        </p>
      </div>

      {isEmpty ? (
        <div className="text-center py-16 bg-white rounded-lg shadow">
          <div className="text-6xl mb-4">🛒</div>
          <p className="text-2xl text-gray-600 mb-6">Tu carrito está vacío</p>
          <button
            onClick={() => navigate('/productos')}
            className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold shadow-lg transition"
          >
            Ir a Productos
          </button>
        </div>
      ) : (
        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <div className="bg-white rounded-lg shadow-lg overflow-hidden">
              {carrito.items.map((item) => (
                <div
                  key={item.id}
                  className="p-6 border-b border-gray-200 last:border-b-0 hover:bg-gray-50 transition"
                >
                  <div className="flex items-center gap-6">
                    <div className="w-24 h-24 bg-gradient-to-br from-blue-100 to-purple-100 rounded-lg flex items-center justify-center flex-shrink-0">
                      <span className="text-4xl">📦</span>
                    </div>

                    <div className="flex-1">
                      <h3 className="text-xl font-bold text-gray-800 mb-1">
                        {item.productoNombre || `Producto #${item.productoId}`}
                      </h3>
                      <p className="text-gray-600 mb-2">
                        Precio unitario: S/ {(item.precioUnitario || 0).toFixed(2)}
                      </p>

                      <div className="flex items-center gap-4">
                        <div className="flex items-center gap-2">
                          <button
                            onClick={() => handleUpdateQuantity(item.id!, item.cantidad - 1)}
                            disabled={item.cantidad <= 1}
                            className="w-8 h-8 bg-gray-200 rounded-lg hover:bg-gray-300 disabled:opacity-50 disabled:cursor-not-allowed font-bold"
                          >
                            -
                          </button>
                          <span className="w-12 text-center font-semibold">{item.cantidad}</span>
                          <button
                            onClick={() => handleUpdateQuantity(item.id!, item.cantidad + 1)}
                            className="w-8 h-8 bg-gray-200 rounded-lg hover:bg-gray-300 font-bold"
                          >
                            +
                          </button>
                        </div>

                        <button
                          onClick={() => handleRemoveItem(item.id!)}
                          className="text-red-600 hover:text-red-700 font-medium"
                        >
                          Eliminar
                        </button>
                      </div>
                    </div>

                    <div className="text-right">
                      <div className="text-2xl font-bold text-gray-800">
                        S/ {(item.total || 0).toFixed(2)}
                      </div>
                      <div className="text-sm text-gray-500">Subtotal</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="lg:col-span-1">
            <div className="bg-white rounded-lg shadow-lg p-6 sticky top-4">
              <h2 className="text-2xl font-bold text-gray-800 mb-6">Resumen del Pedido</h2>

              <div className="space-y-3 mb-6 pb-6 border-b border-gray-200">
                <div className="flex justify-between text-gray-600">
                  <span>Subtotal</span>
                  <span>S/ {total.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Envío</span>
                  <span className="text-green-600 font-semibold">Gratis</span>
                </div>
              </div>

              <div className="mb-6 pb-6 border-b border-gray-200">
                <div className="flex justify-between text-xl font-bold text-gray-800">
                  <span>Total</span>
                  <span>S/ {total.toFixed(2)}</span>
                </div>
              </div>

              {!showCheckout ? (
                <button
                  onClick={() => setShowCheckout(true)}
                  className="w-full bg-green-600 text-white py-4 rounded-lg font-semibold hover:bg-green-700 transition shadow-lg"
                >
                  Proceder al Pago
                </button>
              ) : (
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Dirección de Envío
                    </label>
                    <textarea
                      value={direccionEnvio}
                      onChange={(e) => setDireccionEnvio(e.target.value)}
                      placeholder="Ingresa tu dirección completa..."
                      rows={3}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
                    />
                  </div>

                  <button
                    onClick={handleCheckout}
                    disabled={createOrdenMutation.isPending}
                    className="w-full bg-green-600 text-white py-4 rounded-lg font-semibold hover:bg-green-700 transition shadow-lg disabled:bg-green-400"
                  >
                    {createOrdenMutation.isPending ? 'Procesando...' : 'Confirmar Pedido'}
                  </button>

                  <button
                    onClick={() => setShowCheckout(false)}
                    className="w-full bg-gray-200 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-300 transition"
                  >
                    Cancelar
                  </button>
                </div>
              )}

              <div className="mt-6 pt-6 border-t border-gray-200">
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <span>🔒</span>
                  <span>Pago 100% seguro</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Carrito;
