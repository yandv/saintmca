#!/bin/bash

# Script para extrair versão e nome de plugin.yml ou bungee.yml

set -e

# Obtém o diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

MODULE=$1
YML_FILE=""

# Muda para o diretório raiz do projeto
cd "$PROJECT_ROOT"

if [ "$MODULE" = "bukkit" ]; then
  YML_FILE="bukkit/src/main/resources/plugin.yml"
elif [ "$MODULE" = "proxy" ]; then
  YML_FILE="proxy/src/main/resources/bungee.yml"
else
  echo "Error: Invalid module $MODULE. Must be 'bukkit' or 'proxy'"
  exit 1
fi

if [ ! -f "$YML_FILE" ]; then
  echo "Error: File $YML_FILE not found"
  exit 1
fi

# Extrai name e version do arquivo yml
NAME=$(grep "^name:" "$YML_FILE" | sed 's/^name:[[:space:]]*//' | sed "s/^'//" | sed "s/'$//" | tr -d '[:space:]')
VERSION=$(grep "^version:" "$YML_FILE" | sed 's/^version:[[:space:]]*//' | sed "s/^'//" | sed "s/'$//" | tr -d '[:space:]')

if [ -z "$NAME" ] || [ -z "$VERSION" ]; then
  echo "Error: Could not extract name or version from $YML_FILE"
  exit 1
fi

echo "$NAME|$VERSION"

