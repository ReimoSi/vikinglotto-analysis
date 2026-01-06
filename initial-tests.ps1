# initial-tests.ps1 - quick smoke for vikinglotto-analysis
# Run app separately: ./gradlew bootRun

$ErrorActionPreference = "Stop"
$base = "http://localhost:8080"
$user = "user:user123"
$admin = "admin:admin123"

function statusLine($name, $code) {
    $ok = if ($code -eq 200) { "OK" } elseif ($code -eq 403) { "FORBIDDEN" } elseif ($code -eq 401) { "UNAUTHORIZED" } else { "HTTP $code" }
    Write-Host ("{0,-30} {1}" -f $name, $ok)
}

# 1) Health (public)
$code = (curl.exe -s -o NUL -w "%{http_code}" "$base/actuator/health")
statusLine "actuator/health (public)" $code

# 2) Swagger (public, content check)
try {
    $html = (Invoke-WebRequest "$base/swagger-ui/index.html" -UseBasicParsing).Content
    if ($html -match "Swagger") { Write-Host ("{0,-30} OK" -f "swagger-ui/index.html (public)") }
    else { Write-Host ("{0,-30} CONTENT MISSING" -f "swagger-ui/index.html (public)") }
} catch {
    Write-Host ("{0,-30} FAIL" -f "swagger-ui/index.html (public)")
}

# 3) USER: /api/generate/export.csv
$code = (curl.exe -u $user -s -o NUL -w "%{http_code}" "$base/api/generate/export.csv?method=uniform&rows=2&seed=42")
statusLine "generate/export.csv (USER)" $code

# 4) USER: /api/admin/import/csv -> 403
$code = (curl.exe -u $user -s -o NUL -w "%{http_code}" "$base/api/admin/import/csv")
statusLine "admin/import/csv (USER)" $code

# 5) Draws export (USER) -> saves file
Write-Host "download draws.csv (USER)"
curl.exe -u $user "$base/api/draws/export.csv" -o draws.csv | Out-Null
Get-Content .\draws.csv | Select-Object -First 3 | ForEach-Object { "  " + $_ }

# 6) Admin import (ADMIN)
$adminCheck = (curl.exe -u $admin -s -o NUL -w "%{http_code}" "$base/api/admin/import/csv")
statusLine "admin/import/csv (ADMIN ping)" $adminCheck

Write-Host "`nDone."
