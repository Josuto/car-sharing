#!/usr/bin/env bash
# Step 2 of 2 for local k8s deployment. Run after build-images.sh.
#
# Applies k8s manifests and restarts all deployments so pods pick up newly
# imported images. imagePullPolicy: Never means k3s never contacts a remote
# registry; it only reads from the local containerd store populated by
# build-images.sh.
#
# PVC data (SQLite databases, OpenObserve storage) is preserved across runs.
# To wipe everything including data, run teardown.sh first.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
INFRA="$PROJECT_ROOT/infra"
SERVICES="$PROJECT_ROOT/services"
NAMESPACE=car-sharing

# --- Foundation ---
echo "==> Applying namespace and configmap"
kubectl apply -f "$INFRA/namespace.yaml"
kubectl apply -f "$INFRA/configmap.yaml"

echo "==> Creating OpenObserve SMTP secret from .env"
kubectl create secret generic openobserve-smtp \
  --from-env-file="$PROJECT_ROOT/.env" \
  -n "$NAMESPACE" \
  --dry-run=client -o yaml | kubectl apply -n "$NAMESPACE" -f -

# --- Infrastructure ---
echo "==> Applying RabbitMQ and OpenObserve"
kubectl apply -f "$INFRA/rabbitmq.yaml"
kubectl apply -f "$INFRA/openobserve.yaml"

echo "==> Waiting for RabbitMQ to be ready..."
kubectl wait --for=condition=ready pod -l app=rabbitmq -n "$NAMESPACE" --timeout=120s

echo "==> Waiting for OpenObserve to be ready..."
kubectl wait --for=condition=ready pod -l app=openobserve -n "$NAMESPACE" --timeout=120s

# --- Services ---
echo "==> Applying Spring Boot services"
kubectl apply -f "$SERVICES/gateway/infra/gateway.yaml"
kubectl apply -f "$SERVICES/user-management/infra/user-management.yaml"
kubectl apply -f "$SERVICES/car-registry/infra/car-registry.yaml"
kubectl apply -f "$SERVICES/car-booking/infra/car-booking.yaml"
kubectl apply -f "$SERVICES/psp-stub/infra/psp-stub.yaml"
kubectl apply -f "$SERVICES/payments/infra/payments.yaml"

echo "==> Restarting all deployments to pick up new images..."
kubectl get deployment -n "$NAMESPACE" -o name | xargs kubectl rollout restart -n "$NAMESPACE"

echo "==> Applying OpenObserve alert setup Job"
kubectl apply -f "$INFRA/openobserve-alert-setup.yaml"

echo ""
echo "==> Current pod status:"
kubectl get pods -n "$NAMESPACE"
echo ""
echo "Deployment complete. Run 'kubectl get pods -n $NAMESPACE -w' to watch pods come up."
