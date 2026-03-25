const fs = require('fs');
const FormData = require('form-data');
const fetch = require('node-fetch'); // Asumiendo versión 2 de node-fetch o nativo en Node 18+

async function sendTemplate() {
  if (!fs.existsSync('plantilla.docx')) {
    console.error('❌ Error: Archivo "plantilla.docx" no encontrado en el directorio actual.');
    console.log('Debes crear/pegar uno primero para esta prueba.');
    process.exit(1);
  }

  const payload = {
    nombreCliente: "Acme Corp Ltd.",
    fechaContrato: new Date().toLocaleDateString(),
    productos: [
      { nombre: "Pack 1: Docker Swarm", precio: "1.200 €" },
      { nombre: "Pack 2: Kubernetes", precio: "3.500 €" }
    ],
    total: "4.700 €"
  };

  // Crear la estructura multipart/form-data
  const form = new FormData();
  form.append('template', fs.createReadStream('plantilla.docx'));
  form.append('data', JSON.stringify(payload));

  console.log('🚀 Enviando petición a http://localhost:5189/generate...');

  try {
    const response = await fetch('http://localhost:5189/generate', {
      method: 'POST',
      body: form,
      headers: form.getHeaders(),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error(`❌ Fallo en la generación! Status: ${response.status}`);
      console.error('Detalle:', errorText);
      return;
    }

    // Guardar el PDF resultante
    const pdfBuffer = await response.buffer();
    fs.writeFileSync('contrato_generado_node.pdf', pdfBuffer);
    console.log('✅ Éxito! Se ha guardado el archivo: contrato_generado_node.pdf');

  } catch (error) {
    console.error('❌ Error general durante la invocación:', error.message);
  }
}

sendTemplate();
