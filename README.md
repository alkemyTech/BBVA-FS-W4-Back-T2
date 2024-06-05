# BBVA-FS-W4-Back-T2
# **Proyecto Wallet: Alkywall** :moneybag:
## _Descripción del Proyecto_ :memo:
Alkywall es una billetera virtual desarrollada para ofrecer a los clientes de nuestro banco una forma segura y conveniente de gestionar su dinero en un entorno digital. La aplicación permite realizar diversas funciones bancarias, como transacciones, asociación de tarjetas físicas y virtuales, almacenamiento de dinero y pagos online, todo desde una plataforma web accesible.

## ***Funcionalidades Principales*** :sparkles:
### Gestión de Cuentas y Usuarios :bust_in_silhouette:
- Registro y Autenticación: Los usuarios pueden registrarse y autenticarse en la plataforma. La autenticación se maneja mediante JSON Web Tokens (JWT) para asegurar las sesiones.
- Perfil de Usuario: Los usuarios pueden actualizar y visualizar sus datos personales.
### Transacciones Financieras :money_with_wings:
- Depósitos y Retiros: Los usuarios pueden realizar depósitos en sus cuentas y retirar dinero cuando lo necesiten.
- Transferencias: Es posible realizar transferencias de dinero a otras cuentas dentro del sistema, facilitando el envío de fondos entre usuarios.
- Historial de Transacciones: Los usuarios pueden acceder a un historial detallado de todas sus transacciones, incluyendo depósitos, retiros y transferencias.
### Administración y Roles :moneybag:
- Roles de Usuario: Existen dos roles principales: usuarios estándar y administradores. Los administradores tienen permisos elevados para gestionar otros usuarios y realizar tareas de mantenimiento.
- Gestión de Usuarios: Los administradores pueden crear, actualizar y eliminar cuentas de usuario, así como visualizar y gestionar transacciones realizadas por otros.
### Seguridad :lock:
- Autenticación y Autorización: Utiliza Spring Security para manejar la autenticación y autorización de los usuarios.
- Validación de Datos: Todas las entradas de usuario se validan para asegurar la integridad y seguridad de los datos.
### Simulación y Gestión Financiera :chart_with_upwards_trend:
- Inversiones: Los usuarios pueden simular inversiones a plazo fijo, visualizando la proyección de ganancias y gestionando sus fondos invertidos.
- Cambio de Divisas: Permite a los usuarios convertir dinero entre diferentes monedas, mostrando el valor de cambio del día.
### Interfaz de Usuario :computer: 
- Dashboard Personalizado: Al iniciar sesión, los usuarios tienen acceso a un dashboard donde pueden ver su balance, movimientos recientes, y opciones para gestionar sus fondos.
- Formularios Interactivos: Todos los formularios de la aplicación están diseñados para ser intuitivos y validar la información en tiempo real.
### Documentación y Pruebas :rocket:
- Documentación de la API: Utiliza Swagger para documentar y probar la API, facilitando a los desarrolladores la comprensión y uso de los endpoints disponibles.
- Pruebas Automatizadas: Incluye pruebas automatizadas para asegurar la funcionalidad y fiabilidad de las diferentes partes de la aplicación.
### Tecnología Utilizada :computer:
- Backend: Java con Spring Framework para crear una API robusta y escalable.
- Frontend: ReactJS para una interfaz de usuario dinámica y receptiva.
- Base de Datos: SQL para gestionar las operaciones de CRUD y mantener la integridad de los datos.
### Requisitos del Sistema :bust_in_silhouette:
- Servidor: JDK 11 o superior, servidor de aplicaciones compatible con Spring Boot.
- Cliente: Navegador web moderno (Chrome, Firefox, Safari).
- Base de Datos: Sistema de gestión de bases de datos SQL (MySQL, PostgreSQL).
## Test 📝
Usuarios para realizar pruebas de funcionalidad
### Usuarios Administradores
| Usuario   | Contraseña    |
|-----------|---------------|
| juan@exampleAdmin.com  | adminPassword   |
| pedro@exampleAdmin.com  | adminPassword   |
| pablo@exampleAdmin.com  | adminPassword   |
| messi@exampleAdmin.com  | adminPassword   |
| luis@exampleAdmin.com  | adminPassword   |
| ana@exampleAdmin.com | adminPassword |
| liliana@exampleAdmin.com | adminOassword |
| roberto@exampleAdmin.com | adminPassword |
| maria@exampleAdmin.com | adminPassword |
| duki@exampleAdmin.com | adminPassword |

### Usuarios Regulares
| Usuario   | Contraseña    |
|-----------|---------------|
| juan@example.com  | password   |
| pedro@example.com  | password   |
| pablo@example.com  | password   |
| messi@example.com  | password   |
| luis@example.com  | password   |
| ana@example.com | password |
| liliana@example.com | password |
| roberto@example.com | password |
| maria@example.com | password |
| duki@example.com | password |

