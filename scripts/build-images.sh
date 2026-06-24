#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_ROOT"

SERVICES=(gateway user-management car-registry car-booking payments)

for svc in "${SERVICES[@]}"; do
  echo "==> Building car-sharing/$svc:latest"
  docker build -f "$svc/Dockerfile" -t "car-sharing/$svc:latest" .
done

echo ""
echo "All images built successfully."

if colima status 2>/dev/null | grep -q "Running"; then
  echo "Colima is running — importing images into k3s..."
  for svc in "${SERVICES[@]}"; do
    echo "==> Importing car-sharing/$svc:latest"
    docker save "car-sharing/$svc:latest" | colima ssh -- sudo k3s ctr images import -
  done
  echo ""
  echo "All images imported into k3s."
else
  echo "Colima is not running — skipping k3s import. Run the script again once the cluster is up, or import manually with:"
  echo "  docker save car-sharing/<service>:latest | colima ssh -- sudo k3s ctr images import -"
fi
