import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ordenService } from '../services/ordenService';
import type { EstadoOrden } from '../types';

function MisOrdenes() {
  const [selectedOrdenId, setSelectedOrdenId] = useState<number | null>(null);

  const { data: ordenes, isLoading } = useQuery({
    queryKey: ['mis-ordenes'],
    queryFn: ordenService.getMisOrdenes,
  });

  const { data: ordenDetalle } = useQuery({
    queryKey: ['orden', selectedOrdenId],
    queryFn: () => ordenService.getOrdenById(selectedOrdenId!),
    enabled: !!selectedOrdenId,
  });

  const getEstadoColor = (estado: EstadoOrden) => {
    switch (estado) {
      case 'PENDIENTE':
        return 'bg-yellow-100 text-yellow-700';
      case 'PROCESANDO':
        return 'bg-blue-100 text-blue-700';
      case 'ENVIADO':
        return 'bg-purple-100 text-purple-700';
      case 'ENTREGADO':
        return 'bg-green-100 text-green-700';
      case 'CANCELADO':
        return 'bg-red-100 text-red-700';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  };

  const getEstadoIcon = (estado: EstadoOrden) => {
    switch (estado) {
      case 'PENDIENTE':
        return '⏳';
      case 'PROCESANDO':
        return '📦';
      case 'ENVIADO':
        return '🚚';
      case 'ENTREGADO':
        return '✅';
      case 'CANCELADO':
        return '❌';
      default:
        return '📋';
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Cargando órdenes...</div>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-2">Mis Órdenes</h1>
        <p className="text-gray-600">Historial de todas tus compras</p>
      </div>

      {!ordenes || ordenes.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-lg shadow">
          <div className="text-6xl mb-4">📦</div>
          <p className="text-2xl text-gray-600 mb-4">Aún no tienes órdenes</p>
          <p className="text-gray-500 mb-6">Empieza a comprar para ver tus pedidos aquí</p>
        </div>
      ) : (
        <div className="space-y-6">
          {ordenes.map((orden) => (
            <div
              key={orden.id}
              className="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
            >
              <div className="bg-gradient-to-r from-blue-50 to-indigo-50 p-6 border-b border-gray-200">
                <div className="flex items-center justify-between flex-wrap gap-4">
                  <div>
                    <h3 className="text-2xl font-bold text-gray-800 mb-1">
                      Orden #{orden.id}
                    </h3>
                    <p className="text-gray-600">
                      {orden.fechaOrden && new Date(orden.fechaOrden).toLocaleDateString('es-PE', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit',
                      })}
                    </p>
                  </div>

                  <div className="text-right">
                    <div className="text-3xl font-bold text-green-600 mb-2">
                      S/ {(orden.total || 0).toFixed(2)}
                    </div>
                    {orden.estado && (
                      <span className={`inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-semibold ${getEstadoColor(orden.estado)}`}>
                        <span>{getEstadoIcon(orden.estado)}</span>
                        <span>{orden.estado.replace('_', ' ')}</span>
                      </span>
                    )}
                  </div>
                </div>
              </div>

              <div className="p-6">
                <div className="grid md:grid-cols-2 gap-6 mb-6">
                  <div>
                    <h4 className="font-semibold text-gray-700 mb-2 flex items-center gap-2">
                      <span>📍</span>
                      Dirección de Envío
                    </h4>
                    <p className="text-gray-600 bg-gray-50 p-3 rounded-lg">
                      {orden.direccionEnvio}
                    </p>
                  </div>

                  <div>
                    <h4 className="font-semibold text-gray-700 mb-2">Detalles del Pedido</h4>
                    <div className="space-y-2 text-gray-600">
                      <div className="flex justify-between">
                        <span>Items en la orden:</span>
                        <span className="font-semibold">{orden.items?.length || 0} productos</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Total:</span>
                        <span className="font-semibold text-green-600">S/ {(orden.total || 0).toFixed(2)}</span>
                      </div>
                    </div>
                  </div>
                </div>

                {selectedOrdenId === orden.id ? (
                  <div>
                    <h4 className="font-semibold text-gray-700 mb-4">Productos en esta orden:</h4>
                    {ordenDetalle?.items && ordenDetalle.items.length > 0 ? (
                      <div className="space-y-3 bg-gray-50 p-4 rounded-lg">
                        {ordenDetalle.items.map((item) => (
                          <div
                            key={item.id}
                            className="flex items-center justify-between bg-white p-4 rounded-lg shadow-sm"
                          >
                            <div className="flex items-center gap-4">
                              <div className="w-16 h-16 bg-gradient-to-br from-blue-100 to-purple-100 rounded-lg flex items-center justify-center">
                                <span className="text-2xl">📦</span>
                              </div>
                              <div>
                                <h5 className="font-semibold text-gray-800">
                                  Producto #{item.productoId}
                                </h5>
                                <p className="text-sm text-gray-600">
                                  Cantidad: {item.cantidad} x S/ {(item.precioUnitario || 0).toFixed(2)}
                                </p>
                              </div>
                            </div>
                            <div className="text-right">
                              <div className="font-bold text-gray-800">
                                S/ {(item.subTotal || 0).toFixed(2)}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-gray-500">No hay items en esta orden</p>
                    )}

                    <button
                      onClick={() => setSelectedOrdenId(null)}
                      className="mt-4 text-blue-600 hover:text-blue-700 font-medium"
                    >
                      Ocultar detalles
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={() => setSelectedOrdenId(orden.id!)}
                    className="text-blue-600 hover:text-blue-700 font-medium flex items-center gap-2"
                  >
                    <span>Ver detalles completos</span>
                    <span>→</span>
                  </button>
                )}
              </div>

              {orden.estado === 'PENDIENTE' && (
                <div className="bg-yellow-50 border-t border-yellow-200 p-4">
                  <div className="flex items-center gap-2 text-yellow-700">
                    <span>⚠️</span>
                    <span className="text-sm">
                      Tu orden está siendo procesada. Pronto recibirás una confirmación.
                    </span>
                  </div>
                </div>
              )}

              {orden.estado === 'ENVIADO' && (
                <div className="bg-blue-50 border-t border-blue-200 p-4">
                  <div className="flex items-center gap-2 text-blue-700">
                    <span>🚚</span>
                    <span className="text-sm">
                      Tu pedido está en camino. Pronto llegará a tu dirección.
                    </span>
                  </div>
                </div>
              )}

              {orden.estado === 'ENTREGADO' && (
                <div className="bg-green-50 border-t border-green-200 p-4">
                  <div className="flex items-center gap-2 text-green-700">
                    <span>✅</span>
                    <span className="text-sm">
                      Pedido entregado exitosamente. ¡Gracias por tu compra!
                    </span>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <div className="mt-8 bg-blue-50 border border-blue-200 rounded-lg p-6">
        <h3 className="font-bold text-gray-800 mb-2">Estados de las órdenes</h3>
        <div className="grid md:grid-cols-5 gap-4 text-sm">
          <div className="flex items-center gap-2">
            <span>⏳</span>
            <span><strong>Pendiente:</strong> Orden recibida</span>
          </div>
          <div className="flex items-center gap-2">
            <span>📦</span>
            <span><strong>En Proceso:</strong> Preparando envío</span>
          </div>
          <div className="flex items-center gap-2">
            <span>🚚</span>
            <span><strong>Enviado:</strong> En camino</span>
          </div>
          <div className="flex items-center gap-2">
            <span>✅</span>
            <span><strong>Entregado:</strong> Recibido</span>
          </div>
          <div className="flex items-center gap-2">
            <span>❌</span>
            <span><strong>Cancelado:</strong> Orden cancelada</span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MisOrdenes;
