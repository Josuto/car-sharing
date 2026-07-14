#!/usr/bin/env bash
# Requires: curl, jq
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DASHBOARDS_DIR="$(dirname "$SCRIPT_DIR")/dashboards"

OPENOBSERVE_ENDPOINT="${OPENOBSERVE_ENDPOINT:-http://localhost:5080}"
OPENOBSERVE_USERNAME="${OPENOBSERVE_USERNAME:-admin@example.com}"
OPENOBSERVE_PASSWORD="${OPENOBSERVE_PASSWORD:-Admin@1234}"
OPENOBSERVE_ORG="${OPENOBSERVE_ORG:-default}"

AUTH=$(echo -n "$OPENOBSERVE_USERNAME:$OPENOBSERVE_PASSWORD" | base64)
BASE_URL="$OPENOBSERVE_ENDPOINT/api/$OPENOBSERVE_ORG"

for dashboard_file in "$DASHBOARDS_DIR"/*.json; do
  title=$(jq -r '.title' "$dashboard_file")
  echo "==> Provisioning: $title"

  existing_id=$(
    curl -sf -H "Authorization: Basic $AUTH" "$BASE_URL/dashboards" |
    jq -r --arg t "$title" '
      .dashboards[] |
      select((.title == $t) or (.v8.title == $t)) |
      (.dashboard_id // .v8.dashboardId // .dashboardId) // empty
    '
  )

  if [ -n "$existing_id" ]; then
    echo "    Dashboard exists (id: $existing_id) — deleting and recreating"
    curl -sf -X DELETE -H "Authorization: Basic $AUTH" "$BASE_URL/dashboards/$existing_id" > /dev/null
    curl -sf -X POST \
      -H "Authorization: Basic $AUTH" \
      -H "Content-Type: application/json" \
      -d @"$dashboard_file" \
      "$BASE_URL/dashboards" | jq -r '.dashboard_id // .v8.dashboardId'
  else
    echo "    Dashboard not found — creating"
    curl -sf -X POST \
      -H "Authorization: Basic $AUTH" \
      -H "Content-Type: application/json" \
      -d @"$dashboard_file" \
      "$BASE_URL/dashboards" | jq -r '.dashboard_id // .v8.dashboardId'
  fi
done

echo ""
echo "All dashboards provisioned. Open OpenObserve at $OPENOBSERVE_ENDPOINT to verify."
