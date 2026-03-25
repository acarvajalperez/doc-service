#!/bin/bash

if [ ! -f "plantilla.docx" ]; then
    echo "❌ Archivo 'plantilla.docx' no encontrado."
    echo "ℹ️  Debes crear un documento de Word en la misma carpeta que contenga nombres de variables Velocity:"
    echo "    - \$nombreCliente"
    echo "    - \$fechaContrato"
    echo "    - \$total"
    echo "    - Un ciclo con #foreach(\$producto in \$productos) y #end"
    exit 1
fi

echo "🚀 Enviando plantilla y JSON a doc-service..."
echo "============================================="

# Llamada usando curl a través del endpoint expuesto en docker-compose (5189)
curl -X POST http://localhost:8080/generate \
  -H "Accept: application/pdf" \
  -F "template=@plantilla.docx" \
  -F "data=<payload.json" \
  --output contrato_generado.pdf \
  --verbose

echo " "
echo "✅ Proceso terminado."
if [ -f "contrato_generado.pdf" ]; then
    echo "📄 Se generó el archivo 'contrato_generado.pdf' a partir de la plantilla."
fi
