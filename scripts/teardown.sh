#!/usr/bin/env bash
# Full namespace teardown — permanently deletes ALL resources including PVCs and their data.
# Use deploy.sh for a normal redeploy that preserves database state.
set -euo pipefail

NAMESPACE=car-sharing

if kubectl get namespace "$NAMESPACE" &>/dev/null; then
  echo "WARNING: This will permanently destroy all data in namespace '$NAMESPACE', including all PVC data."
  read -r -p "Type 'yes' to confirm: " confirmation
  if [ "$confirmation" != "yes" ]; then
    echo "Aborted."
    exit 0
  fi
  echo "==> Destroying namespace '$NAMESPACE'..."
  kubectl delete namespace "$NAMESPACE"
  echo "==> Waiting for namespace to be fully removed..."
  kubectl wait --for=delete namespace/"$NAMESPACE" --timeout=120s
  echo "Teardown complete."
else
  echo "Namespace '$NAMESPACE' not found — nothing to do."
fi
