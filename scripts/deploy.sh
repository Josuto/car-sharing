#!/usr/bin/env bash
# Step 2 of 2 for local k8s deployment. Run after build-images.sh.
#
# Tears down the existing namespace (deleting all running pods and state) and
# recreates everything from the k8s manifests. Pod recreation is what causes k8s
# to pull images from the containerd store — this is the step that makes newly
# imported images take effect. imagePullPolicy: Never means k3s never contacts a
# remote registry; it only looks at the local containerd store populated by
# build-images.sh.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
K8S="$PROJECT_ROOT/k8s"
NAMESPACE=car-sharing

# --- Tear down ---
if kubectl get namespace "$NAMESPACE" &>/dev/null; then
  echo "==> Stopping existing deployment (namespace: $NAMESPACE)"
  kubectl delete namespace "$NAMESPACE"
  echo "==> Waiting for namespace to be fully removed..."
  kubectl wait --for=delete namespace/"$NAMESPACE" --timeout=120s
fi

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

echo ""
echo "==> Current pod status:"
kubectl get pods -n "$NAMESPACE"
echo ""
echo "Deployment complete. Run 'kubectl get pods -n $NAMESPACE -w' to watch pods come up."
