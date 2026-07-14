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
K8S="$PROJECT_ROOT/k8s"
NAMESPACE=car-sharing

# --- Foundation ---
echo "==> Applying namespace and configmap"
kubectl apply -f "$K8S/namespace.yaml"
kubectl apply -f "$K8S/configmap.yaml"

# --- Infrastructure ---
echo "==> Applying RabbitMQ and OpenObserve"
kubectl apply -f "$K8S/rabbitmq.yaml"
kubectl apply -f "$K8S/openobserve.yaml"

echo "==> Waiting for RabbitMQ to be ready..."
kubectl wait --for=condition=ready pod -l app=rabbitmq -n "$NAMESPACE" --timeout=120s

# --- Services ---
echo "==> Applying Spring Boot services"
kubectl apply -f "$K8S/gateway.yaml"
kubectl apply -f "$K8S/user-management.yaml"
kubectl apply -f "$K8S/car-registry.yaml"
kubectl apply -f "$K8S/car-booking.yaml"
kubectl apply -f "$K8S/psp-stub.yaml"
kubectl apply -f "$K8S/payments.yaml"

echo "==> Restarting all deployments to pick up new images..."
kubectl get deployment -n "$NAMESPACE" -o name | xargs kubectl rollout restart -n "$NAMESPACE"

echo ""
echo "==> Current pod status:"
kubectl get pods -n "$NAMESPACE"
echo ""
echo "Deployment complete. Run 'kubectl get pods -n $NAMESPACE -w' to watch pods come up."
