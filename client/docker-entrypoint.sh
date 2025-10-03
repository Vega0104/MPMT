#!/bin/sh
set -e
cat <<JS > /usr/share/nginx/html/env.js
window.__env = {
  API_URL: "${API_URL:-http://localhost:8081}"
};
JS
exec "$@"
