import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';

function Home() {
  const { isAuthenticated, user } = useAuthStore();

  return (
    <div>
      <div className="bg-gradient-to-r from-blue-600 to-indigo-700 text-white rounded-lg shadow-xl p-12 mb-12">
        <div className="max-w-3xl mx-auto text-center">
          <h1 className="text-5xl font-bold mb-4">
            Bienvenido a Gamarra UE
          </h1>
          <p className="text-xl mb-8 text-blue-100">
            La plataforma de e-commerce líder para comerciantes y clientes de Gamarra
          </p>
          {!isAuthenticated ? (
            <div className="flex justify-center gap-4">
              <Link
                to="/productos"
                className="px-8 py-3 bg-white text-blue-600 rounded-lg hover:bg-blue-50 font-semibold shadow-lg transition-all transform hover:scale-105"
              >
                Explorar Productos
              </Link>
              <Link
                to="/register"
                className="px-8 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 font-semibold shadow-lg transition-all transform hover:scale-105"
              >
                Registrarse Ahora
              </Link>
            </div>
          ) : (
            <div className="text-center">
              <p className="text-2xl mb-4">Hola, {user?.nombre}!</p>
              <Link
                to="/productos"
                className="inline-block px-8 py-3 bg-white text-blue-600 rounded-lg hover:bg-blue-50 font-semibold shadow-lg transition-all transform hover:scale-105"
              >
                Ver Productos
              </Link>
            </div>
          )}
        </div>
      </div>

      <div className="grid md:grid-cols-3 gap-8 mb-12">
        <div className="bg-white p-8 rounded-lg shadow-lg text-center border-t-4 border-blue-500">
          <div className="text-5xl mb-4">🛍️</div>
          <h3 className="text-xl font-bold mb-2 text-gray-800">Amplio Catálogo</h3>
          <p className="text-gray-600">
            Miles de productos de ropa y utensilios de los mejores comerciantes de Gamarra
          </p>
        </div>

        <div className="bg-white p-8 rounded-lg shadow-lg text-center border-t-4 border-green-500">
          <div className="text-5xl mb-4">💳</div>
          <h3 className="text-xl font-bold mb-2 text-gray-800">Compra Segura</h3>
          <p className="text-gray-600">
            Sistema de pagos seguro y gestión eficiente de tus pedidos
          </p>
        </div>

        <div className="bg-white p-8 rounded-lg shadow-lg text-center border-t-4 border-purple-500">
          <div className="text-5xl mb-4">🚚</div>
          <h3 className="text-xl font-bold mb-2 text-gray-800">Envío Rápido</h3>
          <p className="text-gray-600">
            Entregas rápidas a todo Lima y seguimiento de tus órdenes en tiempo real
          </p>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow-lg p-8">
        <h2 className="text-3xl font-bold text-center mb-8 text-gray-800">
          ¿Por qué elegir Gamarra UE?
        </h2>
        <div className="grid md:grid-cols-2 gap-6">
          <div className="flex items-start space-x-4">
            <div className="text-3xl">✅</div>
            <div>
              <h4 className="font-bold text-lg text-gray-800 mb-2">Para Clientes</h4>
              <p className="text-gray-600">
                Accede a una gran variedad de productos, compara precios y compra de forma segura
              </p>
            </div>
          </div>
          <div className="flex items-start space-x-4">
            <div className="text-3xl">💼</div>
            <div>
              <h4 className="font-bold text-lg text-gray-800 mb-2">Para Comerciantes</h4>
              <p className="text-gray-600">
                Publica tus productos, gestiona tu inventario y llega a más clientes
              </p>
            </div>
          </div>
          <div className="flex items-start space-x-4">
            <div className="text-3xl">📱</div>
            <div>
              <h4 className="font-bold text-lg text-gray-800 mb-2">Fácil de Usar</h4>
              <p className="text-gray-600">
                Interfaz intuitiva y moderna para una experiencia de compra excepcional
              </p>
            </div>
          </div>
          <div className="flex items-start space-x-4">
            <div className="text-3xl">🔒</div>
            <div>
              <h4 className="font-bold text-lg text-gray-800 mb-2">100% Seguro</h4>
              <p className="text-gray-600">
                Tu información está protegida con los más altos estándares de seguridad
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
