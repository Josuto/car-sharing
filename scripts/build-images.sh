#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_ROOT"

SERVICES=(gateway user-management car-registry car-booking payments)

for svc in "${SERVICES[@]}"; do
  echo "==> Building car-sharing/$svc:latest"
  docker buildx build --load -f "$svc/Dockerfile" -t "car-sharing/$svc:latest" .
done

echo ""
echo "All images built successfully."
