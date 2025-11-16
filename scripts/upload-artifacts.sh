#!/bin/bash

# Script para fazer upload dos artefatos para a API

set -e

# Obtém o diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

CHANGED_MODULES=$1
API_URL="https://api.aproxima.me/plugins"
API_TOKEN="${API_TOKEN}"

# Muda para o diretório raiz do projeto
cd "$PROJECT_ROOT"

if [ -z "$CHANGED_MODULES" ]; then
  echo "No modules to upload"
  exit 0
fi

if [ -z "$API_TOKEN" ]; then
  echo "Error: API_TOKEN environment variable is not set"
  exit 1
fi

echo "Uploading artifacts for modules: $CHANGED_MODULES"

# Processa cada módulo alterado
for MODULE in $CHANGED_MODULES; do
  echo ""
  echo "Processing $MODULE module..."
  
  # Extrai name e version
  INFO=$("$SCRIPT_DIR/extract-version.sh" "$MODULE")
  NAME=$(echo "$INFO" | cut -d'|' -f1)
  VERSION=$(echo "$INFO" | cut -d'|' -f2)
  
  echo "  Name: $NAME"
  echo "  Version: $VERSION"
  
  # Encontra o arquivo JAR gerado
  JAR_FILE=""
  if [ "$MODULE" = "bukkit" ]; then
    JAR_FILE=$(find bukkit/target -name "bukkit-*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1)
  elif [ "$MODULE" = "proxy" ]; then
    JAR_FILE=$(find proxy/target -name "proxy-*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -n 1)
  fi
  
  if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
    echo "  Error: JAR file not found for $MODULE"
    continue
  fi
  
  echo "  JAR file: $JAR_FILE"
  
  # Prepara o payload JSON
  # Usa curl para fazer PATCH com multipart/form-data
  RESPONSE=$(curl -s -w "\n%{http_code}" -X PATCH \
    -H "Authorization: Bearer $API_TOKEN" \
    -F "version=$VERSION" \
    -F "file=@$JAR_FILE" \
    "$API_URL/$NAME")
  
  HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
  BODY=$(echo "$RESPONSE" | sed '$d')
  
  if [ "$HTTP_CODE" -ge 200 ] && [ "$HTTP_CODE" -lt 300 ]; then
    echo "  ✓ Successfully uploaded $NAME v$VERSION (HTTP $HTTP_CODE)"
    echo "  Response: $BODY"
  else
    echo "  ✗ Failed to upload $NAME v$VERSION (HTTP $HTTP_CODE)"
    echo "  Response: $BODY"
    exit 1
  fi
done

echo ""
echo "All artifacts uploaded successfully!"

