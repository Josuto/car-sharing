#!/usr/bin/env bash
# Step 1 of 2 for local k8s deployment. Run this before deploy.sh.
#
# 1. Builds all service JARs with Gradle on the host (reliable Maven Central access).
# 2. Packages each JAR into a Docker image using buildx --load so that Colima's
#    k3s (Docker CRI) sees them immediately without a separate import step.
# Run deploy.sh afterwards to recreate pods with the new images.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_ROOT"

SERVICES=(gateway user-management car-registry car-booking payments psp-stub)

# ── Step 1: build JARs on the host ──────────────────────────────────────────
echo "==> Building JARs with Gradle..."
./gradlew build -x test --no-daemon -q
echo "    Done."

# ── Step 2: pre-pull runtime base image ─────────────────────────────────────
pull_if_missing() {
  local img=$1
  if docker image inspect "$img" > /dev/null 2>&1; then
    echo "    $img already present, skipping"
    return 0
  fi
  local attempt=1
  while [ $attempt -le 3 ]; do
    echo "    Pulling $img (attempt $attempt/3)..."
    if docker pull "$img"; then
      return 0
    fi
    echo "    Pull failed, retrying in 10s..."
    sleep 10
    attempt=$((attempt + 1))
  done
  echo "ERROR: Failed to pull $img after 3 attempts"
  return 1
}

echo "==> Pre-pulling base image..."
pull_if_missing "eclipse-temurin:21-jre-alpine"

# ── Step 3: build Docker images from pre-built artifacts ────────────────────
for svc in "${SERVICES[@]}"; do
  echo "==> Building car-sharing/$svc:latest"
  docker buildx build --load -f "services/$svc/Dockerfile" -t "car-sharing/$svc:latest" .
done

echo ""
echo "All images built successfully. Run ./scripts/deploy.sh to apply them to the cluster."
