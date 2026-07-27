#!/usr/bin/env bash
# Step 1 of 2 for local k8s deployment. Run this before deploy.sh.
#
# Builds a Docker image for each service using buildx. Because Colima's k3s
# uses Docker as its CRI (not containerd), --load makes images land directly in
# the Colima VM's Docker daemon and are immediately visible to k3s — no import
# step is needed. Run deploy.sh afterwards to recreate pods with the new images.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_ROOT"

SERVICES=(gateway user-management car-registry car-booking payments psp-stub)

# Build each service image using its own Dockerfile.
# All Dockerfiles use a multi-stage build: gradle builder → JRE runtime image.
for svc in "${SERVICES[@]}"; do
  echo "==> Building car-sharing/$svc:latest"
  docker buildx build --load -f "services/$svc/Dockerfile" -t "car-sharing/$svc:latest" .
done

echo ""
echo "All images built successfully. Run ./scripts/deploy.sh to apply them to the cluster."
