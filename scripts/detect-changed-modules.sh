#!/bin/bash

# Script para detectar quais módulos foram alterados
# Compara as mudanças com a branch base (main/master) ou com o último commit em PRs

set -e

# Obtém o diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Muda para o diretório raiz do projeto
cd "$PROJECT_ROOT"

# Determina a branch base
if [ "$GITHUB_EVENT_NAME" = "pull_request" ]; then
  if [ -n "$BASE_SHA" ] && [ -n "$HEAD_SHA" ]; then
    # Usa as variáveis passadas pelo workflow
    BASE_REF="$BASE_SHA"
    HEAD_REF="$HEAD_SHA"
  else
    # Fallback: compara com a branch base do PR
    BASE_REF=$(git merge-base HEAD origin/main 2>/dev/null || git merge-base HEAD origin/master 2>/dev/null || echo "HEAD~1")
    HEAD_REF="HEAD"
  fi
elif [ "$GITHUB_EVENT_NAME" = "workflow_dispatch" ]; then
  # Para execução manual, compara com a branch base (main/master)
  # Isso mostra todas as mudanças desde a última versão publicada
  BASE_REF=$(git merge-base HEAD origin/main 2>/dev/null || git merge-base HEAD origin/master 2>/dev/null || echo "HEAD~1")
  HEAD_REF="HEAD"
else
  # Para push, usa as variáveis do workflow ou fallback
  if [ -n "$BASE_SHA" ] && [ -n "$HEAD_SHA" ]; then
    BASE_REF="$BASE_SHA"
    HEAD_REF="$HEAD_SHA"
  else
    # Fallback: compara com o commit anterior
    BASE_REF="HEAD~1"
    HEAD_REF="HEAD"
  fi
fi

# Se não conseguir determinar, usa main como base
if [ -z "$BASE_REF" ] || [ "$BASE_REF" = "HEAD~1" ]; then
  BASE_REF=$(git merge-base HEAD origin/main 2>/dev/null || git merge-base HEAD origin/master 2>/dev/null || echo "HEAD~1")
fi

if [ -z "$HEAD_REF" ]; then
  HEAD_REF="HEAD"
fi

echo "Comparing $BASE_REF..$HEAD_REF"

# Detecta arquivos alterados
CHANGED_FILES=$(git diff --name-only $BASE_REF..$HEAD_REF)

# Inicializa variáveis
CORE_CHANGED=false
BUKKIT_CHANGED=false
PROXY_CHANGED=false

# Verifica mudanças em cada módulo
if echo "$CHANGED_FILES" | grep -q "^core/"; then
  CORE_CHANGED=true
  echo "✓ Core module changed"
fi

if echo "$CHANGED_FILES" | grep -q "^bukkit/"; then
  BUKKIT_CHANGED=true
  echo "✓ Bukkit module changed"
fi

if echo "$CHANGED_FILES" | grep -q "^proxy/"; then
  PROXY_CHANGED=true
  echo "✓ Proxy module changed"
fi

# Se core mudou, bukkit e proxy também precisam ser rebuildados
if [ "$CORE_CHANGED" = true ]; then
  BUKKIT_CHANGED=true
  PROXY_CHANGED=true
  echo "✓ Core changed, marking bukkit and proxy for rebuild"
fi

# Constrói lista de módulos alterados
CHANGED_MODULES=""

if [ "$BUKKIT_CHANGED" = true ]; then
  CHANGED_MODULES="${CHANGED_MODULES}bukkit "
fi

if [ "$PROXY_CHANGED" = true ]; then
  CHANGED_MODULES="${CHANGED_MODULES}proxy "
fi

# Remove espaços extras
CHANGED_MODULES=$(echo "$CHANGED_MODULES" | xargs)

if [ -z "$CHANGED_MODULES" ]; then
  echo "No deployable modules changed (bukkit/proxy)"
  echo "changed=false" >> $GITHUB_OUTPUT
else
  echo "Changed modules: $CHANGED_MODULES"
  echo "changed=$CHANGED_MODULES" >> $GITHUB_OUTPUT
fi

