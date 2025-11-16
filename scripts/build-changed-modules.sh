#!/bin/bash

# Script para fazer build dos módulos alterados

set -e

# Obtém o diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

CHANGED_MODULES=$1

# Muda para o diretório raiz do projeto
cd "$PROJECT_ROOT"

if [ -z "$CHANGED_MODULES" ]; then
  echo "No modules to build"
  exit 0
fi

echo "Building modules: $CHANGED_MODULES"

# Se core precisa ser buildado (dependência), builda primeiro
if echo "$CHANGED_MODULES" | grep -q "bukkit\|proxy"; then
  echo "Building core module (dependency)..."
  mvn clean install -pl core -am -DskipTests -U
fi

# Builda cada módulo alterado
for MODULE in $CHANGED_MODULES; do
  echo "Building $MODULE module..."
  mvn clean package -pl $MODULE -am -DskipTests -U
done

echo "Build completed successfully"

