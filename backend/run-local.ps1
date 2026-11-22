# =============================================================================
# SkillSwap Backend - Local Development Startup Script
# Loads .env file and starts the Spring Boot backend
# =============================================================================

# Determine the backend directory (directory containing this script)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$envFile = Join-Path $scriptDir ".env"

# Phase 1: Check if .env file exists
if (-Not (Test-Path $envFile)) {
    Write-Error "ERROR: .env file not found at: $envFile"
    Write-Error "Please create backend/.env from backend/.env.example and fill in your Supabase credentials."
    exit 1
}

# Phase 2: Load environment variables from .env file
# Handles blank lines and comment lines (lines starting with #)
$loadedVars = @()
Get-Content -Path $envFile | ForEach-Object {
    $line = $_.Trim()
    
    # Skip blank lines and comments
    if ($line -eq "" -or $line.StartsWith("#")) {
        return
    }
    
    # Parse KEY=VALUE pairs
    if ($line -match "^([^=]+)=(.*)$") {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim()
        
        # Remove surrounding quotes if present
        if ($value.StartsWith('"') -and $value.EndsWith('"')) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        
        # Set the environment variable in the current PowerShell process
        [System.Environment]::SetEnvironmentVariable($key, $value, [System.EnvironmentVariableTarget]::Process)
        $loadedVars += $key
    }
}

# Phase 3: Validate required variables
$requiredVars = @("DB_URL", "DB_USERNAME", "DB_PASSWORD")
$missingVars = @()

foreach ($var in $requiredVars) {
    $value = [System.Environment]::GetEnvironmentVariable($var, [System.EnvironmentVariableTarget]::Process)
    if ([string]::IsNullOrWhiteSpace($value)) {
        $missingVars += $var
    }
}

if ($missingVars.Count -gt 0) {
    Write-Error "ERROR: The following required environment variables are missing or empty in .env:"
    $missingVars | ForEach-Object { Write-Error "  - $_" }
    Write-Error "Please update backend/.env with your Supabase credentials."
    exit 1
}

# Phase 4: Display status (no secrets exposed)
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "  SkillSwap Backend - Local Startup" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  .env file loaded:      $envFile" -ForegroundColor Green
Write-Host "  DB_URL present:        YES" -ForegroundColor Green
Write-Host "  DB_USERNAME present:   YES" -ForegroundColor Green
Write-Host "  DB_PASSWORD present:   YES (hidden)" -ForegroundColor Green
Write-Host ""
Write-Host "  Starting Spring Boot backend..." -ForegroundColor Yellow
Write-Host ""

# Phase 5: Start Spring Boot using Maven wrapper
$mvnw = Join-Path $scriptDir "mvnw.cmd"
if (-Not (Test-Path $mvnw)) {
    $mvnw = Join-Path $scriptDir "mvnw"
}

if (-Not (Test-Path $mvnw)) {
    Write-Error "ERROR: Maven wrapper (mvnw) not found at: $mvnw"
    exit 1
}

Write-Host "Running: $mvnw spring-boot:run" -ForegroundColor Gray
Write-Host ""

& $mvnw spring-boot:run
