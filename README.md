# Quasar Document Generator Service (doc-service)

## 🎯 Objetivo

**doc-service** es un microservicio desarrollado en Java con Spring Boot cuyo objetivo principal es la **generación automatizada de documentos en formato PDF**. 

El servicio funciona combinando una **plantilla en formato DOCX** (Word) y un conjunto de **datos en formato JSON**. Mediante el motor de plantillas **Velocity**, el servicio inyecta los datos proporcionados dentro de la plantilla DOCX y, posteriormente, transforma el resultado final en un archivo PDF listo para su distribución o almacenamiento.

Este enfoque permite a los usuarios diseñar plantillas visuales directamente en Microsoft Word y luego completarlas dinámicamente mediante la API, sin necesidad de escribir código para diseñar el PDF.

## 🛠️ Tecnologías Principales

*   **Java & Spring Boot**: Framework base para exponer el servicio REST.
*   **XDocReport**: Librería central encargada de procesar el DOCX y convertirlo a PDF de forma nativa.
*   **Apache Velocity**: Motor de plantillas que permite procesar condicionales, iteraciones y variables directamente sobre el texto del documento Word.

## 🚀 Cómo ejecutar el servicio

### Opción 1: Usando Maven (Local)

Si tienes Java y Maven instalados, puedes ejecutar el servicio directamente:

```bash
mvn clean install
mvn spring-boot:run
```

El servicio se levantará por defecto en el puerto `8080` (o el puerto configurado en `application.properties`/`application.yml`).

### Opción 2: Usando Docker

El proyecto incluye un `Dockerfile`, por lo que puedes construir y ejecutar un contenedor:

```bash
docker build -t doc-service .
docker run -p 8080:8080 doc-service
```

## 📖 Uso de la API

El servicio expone principalmente un endpoint REST para la generación de documentos utilizando `multipart/form-data`.

### `POST /generate`

Recibe una plantilla y un payload de datos, retornando el PDF generado.

#### Parámetros de la petición (Form-Data)

*   `template` (File): El archivo de la plantilla en formato `.docx`. Debe contener variables con sintaxis de Velocity.
*   `data` (File/Text): El payload en formato JSON que contiene los datos que rellenarán la plantilla. (Puede ser un archivo o un JSON stringified, en el ejemplo se envía como archivo).

#### Respuesta

*   **Content-Type**: `application/pdf`
*   **Body**: El flujo de bytes del documento PDF generado.

## 📝 Creación de Plantillas (Sintaxis Velocity)

Las plantillas deben ser creadas en Microsoft Word y guardadas como `.docx`. En cualquier parte del documento, puedes usar la sintaxis de **Velocity** para mapear los datos del JSON:

*   **Variables simples:** `$nombreCliente`, `$fechaContrato`, `$total`
*   **Iteraciones (Listas):** Puedes crear listas o tablas dinámicas:
    ```velocity
    #foreach( $producto in $productos )
        - $producto.nombre: $producto.precio
    #end
    ```
*   **Condicionales:**
    ```velocity
    #if( $esVip )
        ¡Gracias por ser un cliente VIP!
    #end
    ```

## 💻 Ejemplo Práctico

En la carpeta [/ejemplo](./ejemplo) encontrarás un script `invocar.sh` junto con datos de prueba. 

Si tienes el servicio ejecutándose en `localhost:8080`, puedes realizar una invocación usando `cURL` de la siguiente manera:

```bash
curl -X POST http://localhost:8080/generate \
  -H "Accept: application/pdf" \
  -F "template=@plantilla.docx" \
  -F "data=<payload.json" \
  --output contrato_generado.pdf \
  --verbose
```

**Explicación del comando:**
1.  Se envía en el campo `template` el archivo `plantilla.docx`.
2.  Se envía en el campo `data` el contenido de `payload.json`.
3.  El resultado binario devuelto por la API se guarda automáticamente en disco como `contrato_generado.pdf`.
