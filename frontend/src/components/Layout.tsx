import { Outlet, Link } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

function Layout() {
  const { user, isAuthenticated, logout } = useAuthStore();

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              <Link to="/" className="flex items-center px-2 text-gray-900 font-bold text-xl">
                Gamarra UE
              </Link>
              <div className="flex space-x-4 ml-6">
                <Link to="/productos" className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                  Productos
                </Link>
                {isAuthenticated && user?.tipoUsuario === 'COMERCIANTE' && (
                  <Link to="/mis-productos" className="flex items-center px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                    Mis Productos
                  </Link>
                )}
              </div>
            </div>

            <div className="flex items-center">
              {isAuthenticated ? (
                <>
                  {user?.tipoUsuario === 'CLIENTE' && (
                    <>
                      <Link to="/carrito" className="px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                        Carrito
                      </Link>
                      <Link to="/ordenes" className="px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                        Mis Ordenes
                      </Link>
                    </>
                  )}
                  <span className="px-3 py-2 text-sm text-gray-600">
                    {user?.nombre}
                  </span>
                  <button
                    onClick={logout}
                    className="ml-3 px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700"
                  >
                    Salir
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900">
                    Iniciar Sesion
                  </Link>
                  <Link to="/register" className="ml-3 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700">
                    Registrarse
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
