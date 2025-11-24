import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { productoService } from '../services/productoService';
import { carritoService } from '../services/carritoService';
import { useAuthStore } from '../store/useAuthStore';
import type { Producto, CategoriaProducto } from '../types';

function Productos() {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuthStore();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<CategoriaProducto | 'ALL'>('ALL');
  const [successMessage, setSuccessMessage] = useState('');

  const { data: productos, isLoading } = useQuery({
    queryKey: ['productos'],
    queryFn: productoService.getAll,
  });

  const handleAddToCart = async (producto: Producto) => {
    if (!isAuthenticated) {
      alert('Debes iniciar sesión para agregar productos al carrito');
      return;
    }

    if (user?.tipoUsuario !== 'CLIENTE') {
      alert('Solo los clientes pueden agregar productos al carrito');
      return;
    }

    try {
      await carritoService.addItem({
        productoId: producto.id!,
        cantidad: 1,
      });
      setSuccessMessage(`${producto.nombre} agregado al carrito`);
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert('Error al agregar al carrito');
    }
  };

  const filteredProductos = productos?.filter((producto) => {
    const matchesSearch = producto.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (producto.descripcion?.toLowerCase() || '').includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || producto.categoria === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  const categories: Array<CategoriaProducto | 'ALL'> = [
    'ALL',
    'ELECTRONICA',
    'ROPA',
    'ALIMENTOS',
    'HOGAR',
    'DEPORTES',
    'JUGUETES',
    'LIBROS',
    'SALUD_BELLEZA',
    'OTROS',
  ];

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Cargando productos...</div>
      </div>
    );
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-800 mb-4">Catálogo de Productos</h1>
        <p className="text-gray-600">Descubre los mejores productos de Gamarra</p>
      </div>

      {successMessage && (
        <div className="mb-6 p-4 bg-green-50 border border-green-200 text-green-700 rounded-lg">
          {successMessage}
        </div>
      )}

      <div className="mb-8 bg-white p-6 rounded-lg shadow-md">
        <div className="grid md:grid-cols-2 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Buscar Productos
            </label>
            <input
              type="text"
              placeholder="Buscar por nombre o descripción..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Categoría
            </label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value as CategoriaProducto | 'ALL')}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            >
              {categories.map((cat) => (
                <option key={cat} value={cat}>
                  {cat === 'ALL' ? 'Todas las categorías' : cat}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="text-sm text-gray-600">
          Mostrando {filteredProductos?.length || 0} productos
        </div>
      </div>

      {!filteredProductos || filteredProductos.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg shadow">
          <div className="text-6xl mb-4">📦</div>
          <p className="text-xl text-gray-600">No se encontraron productos</p>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredProductos.map((producto) => (
            <div
              key={producto.id}
              className="bg-white rounded-lg shadow-lg overflow-hidden hover:shadow-xl transition-shadow cursor-pointer"
              onClick={() => navigate(`/productos/${producto.id}`)}
            >
              <div className="h-48 bg-gradient-to-br from-blue-100 to-purple-100 flex items-center justify-center">
                <span className="text-6xl">
                  {producto.categoria === 'ELECTRONICA' && '💻'}
                  {producto.categoria === 'ROPA' && '👕'}
                  {producto.categoria === 'ALIMENTOS' && '🍎'}
                  {producto.categoria === 'HOGAR' && '🏠'}
                  {producto.categoria === 'DEPORTES' && '⚽'}
                  {producto.categoria === 'JUGUETES' && '🧸'}
                  {producto.categoria === 'LIBROS' && '📚'}
                  {producto.categoria === 'SALUD_BELLEZA' && '💄'}
                  {producto.categoria === 'OTROS' && '📦'}
                </span>
              </div>

              <div className="p-6">
                <div className="flex items-start justify-between mb-2">
                  <h3 className="text-xl font-bold text-gray-800">{producto.nombre}</h3>
                  <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-semibold">
                    {producto.categoria}
                  </span>
                </div>

                {producto.nombreComercio && (
                  <p className="text-sm text-gray-500 mb-2">
                    Por: {producto.nombreComercio}
                  </p>
                )}

                <p className="text-gray-600 mb-4 line-clamp-2">{producto.descripcion}</p>

                <div className="flex items-center justify-between mb-4">
                  <div>
                    <div className="text-2xl font-bold text-green-600">
                      ${producto.precio.toFixed(2)}
                    </div>
                    <div className="text-sm text-gray-500">
                      Stock: {producto.stock} unidades
                    </div>
                  </div>
                </div>

                {isAuthenticated && user?.tipoUsuario === 'CLIENTE' && (
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleAddToCart(producto);
                    }}
                    disabled={producto.stock === 0}
                    className={`w-full py-2 rounded-lg font-semibold transition ${
                      producto.stock === 0
                        ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                        : 'bg-blue-600 text-white hover:bg-blue-700'
                    }`}
                  >
                    {producto.stock === 0 ? 'Sin Stock' : 'Agregar al Carrito'}
                  </button>
                )}

                {!isAuthenticated && (
                  <div className="text-center text-sm text-gray-500">
                    Inicia sesión para comprar
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Productos;
