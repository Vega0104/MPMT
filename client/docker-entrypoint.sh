#!/bin/sh
set -e
echo "[entrypoint] writing /usr/share/nginx/html/env.js"
cat > /usr/share/nginx/html/env.js <<'EOF'
window.__env = { API_URL: 'http://localhost:8081/api' };
EOF
exec "$@"
