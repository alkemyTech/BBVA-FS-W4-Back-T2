# BBVA-FS-W4-Back-T2
# **Proyecto Wallet: Alkywall**
## _Descripción del Proyecto_
Alkywall es una billetera virtual desarrollada para ofrecer a los clientes de nuestro banco una forma segura y conveniente de gestionar su dinero en un entorno digital. La aplicación permite realizar diversas funciones bancarias, como transacciones, asociación de tarjetas físicas y virtuales, almacenamiento de dinero y pagos online, todo desde una plataforma web accesible.

## ***Funcionalidades Principales***
### Gestión de Cuentas y Usuarios
Registro y Autenticación: Los usuarios pueden registrarse y autenticarse en la plataforma. La autenticación se maneja mediante JSON Web Tokens (JWT) para asegurar las sesiones.
Perfil de Usuario: Los usuarios pueden actualizar y visualizar sus datos personales.
### Transacciones Financieras
Depósitos y Retiros: Los usuarios pueden realizar depósitos en sus cuentas y retirar dinero cuando lo necesiten.
Transferencias: Es posible realizar transferencias de dinero a otras cuentas dentro del sistema, facilitando el envío de fondos entre usuarios.
Historial de Transacciones: Los usuarios pueden acceder a un historial detallado de todas sus transacciones, incluyendo depósitos, retiros y transferencias.
Administración y Roles
Roles de Usuario: Existen dos roles principales: usuarios estándar y administradores. Los administradores tienen permisos elevados para gestionar otros usuarios y realizar tareas de mantenimiento.
Gestión de Usuarios: Los administradores pueden crear, actualizar y eliminar cuentas de usuario, así como visualizar y gestionar transacciones realizadas por otros.
Seguridad
Autenticación y Autorización: Utiliza Spring Security para manejar la autenticación y autorización de los usuarios.
Validación de Datos: Todas las entradas de usuario se validan para asegurar la integridad y seguridad de los datos.
### Simulación y Gestión Financiera
Inversiones: Los usuarios pueden simular inversiones a plazo fijo, visualizando la proyección de ganancias y gestionando sus fondos invertidos.
Cambio de Divisas: Permite a los usuarios convertir dinero entre diferentes monedas, mostrando el valor de cambio del día.
### Interfaz de Usuario
Dashboard Personalizado: Al iniciar sesión, los usuarios tienen acceso a un dashboard donde pueden ver su balance, movimientos recientes, y opciones para gestionar sus fondos.
Formularios Interactivos: Todos los formularios de la aplicación están diseñados para ser intuitivos y validar la información en tiempo real.
### Documentación y Pruebas
Documentación de la API: Utiliza Swagger para documentar y probar la API, facilitando a los desarrolladores la comprensión y uso de los endpoints disponibles.
Pruebas Automatizadas: Incluye pruebas automatizadas para asegurar la funcionalidad y fiabilidad de las diferentes partes de la aplicación.
### Tecnología Utilizada
Backend: Java con Spring Framework para crear una API robusta y escalable.
Frontend: ReactJS para una interfaz de usuario dinámica y receptiva.
Base de Datos: SQL para gestionar las operaciones de CRUD y mantener la integridad de los datos.
### Requisitos del Sistema
Servidor: JDK 11 o superior, servidor de aplicaciones compatible con Spring Boot.
Cliente: Navegador web moderno (Chrome, Firefox, Safari).
Base de Datos: Sistema de gestión de bases de datos SQL (MySQL, PostgreSQL).
