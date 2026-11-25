# TP-final-Administracion-hotelera

Sistema de hotel por consola con perfiles de administrador y recepcion.

## Como ejecutarlo

1. Abri la carpeta del proyecto en IntelliJ y usa JDK 17 o superior. Copia **mysql-connector-j-26.7.0.jar** a **lib** en la raiz del proyecto. El modulo ya lo busca ahi. En esta PC, el JAR original esta en **C:/prog/mysql-connector-j-26.7.0/**.
2. En la raiz del proyecto, completa `conexion.local.properties` una sola vez con los datos de tu cuenta de MySQL. Git ignora ese archivo. Si abris el proyecto en otra computadora, crea un archivo con este contenido y completa la clave.

   ```properties
   usuario=root
   clave=
   ```

   Si tu clave contiene una barra invertida, escribila como `\\`.

3. Con MySQL iniciado en el puerto 3306, ejecuta `aplicacion.Main` desde la carpeta del proyecto. La aplicacion se conecta al arrancar y crea `hotel_tp`, `hotel_tp_copia` y las tablas que falten. La cuenta de MySQL necesita `SELECT`, `INSERT`, `UPDATE`, `CREATE` y `REFERENCES` en `hotel_tp`, y `SELECT`, `INSERT`, `DELETE` y `CREATE` en `hotel_tp_copia`. Connector/J 26.7.0 tiene soporte oficial desde MySQL Server 8.4. Se probo con una instancia local 8.0.43.
4. La prim
