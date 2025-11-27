$ErrorActionPreference = "Stop"

# --- 설정 ---
$TomcatHome = "C:\tools\apache-tomcat-10.1.45"
$ProjectRoot = Get-Location
$BaseDir = Join-Path $ProjectRoot ".antigravity\tomcat_base"
$WebappDir = Join-Path $ProjectRoot "src\main\webapp"
$ClassesDir = Join-Path $ProjectRoot "build\classes"

# --- 유효성 검사 ---
if (-not (Test-Path $TomcatHome)) {
    Write-Error "Tomcat 디렉토리를 찾을 수 없습니다: $TomcatHome"
}

if (-not (Test-Path $ClassesDir)) {
    Write-Warning "빌드 클래스 디렉토리를 찾을 수 없습니다: $ClassesDir"
    Write-Warning "실행하기 전에 프로젝트가 빌드되었는지 확인해주세요 (예: Eclipse에서 빌드)."
}

# --- CATALINA_BASE 설정 ---
Write-Host "Tomcat 베이스 설정 중: $BaseDir"
if (-not (Test-Path $BaseDir)) {
    New-Item -ItemType Directory -Path $BaseDir -Force | Out-Null
}

# 필요한 디렉토리 생성
foreach ($dir in "conf", "logs", "temp", "work", "webapps") {
    $target = Join-Path $BaseDir $dir
    if (-not (Test-Path $target)) {
        New-Item -ItemType Directory -Path $target -Force | Out-Null
    }
}

# CATALINA_HOME에서 설정 파일 복사 (없는 경우)
foreach ($file in "server.xml", "web.xml", "context.xml", "tomcat-users.xml") {
    $sourcePath = Join-Path $TomcatHome "conf\$file"
    $destPath = Join-Path $BaseDir "conf\$file"
    if (-not (Test-Path $destPath)) {
        Copy-Item -Path $sourcePath -Destination $destPath
    }
}

# --- ROOT 컨텍스트 생성 ---
# 루트 컨텍스트("/")를 소스 디렉토리에 매핑하고 WEB-INF/classes를 빌드 디렉토리에 매핑합니다.
$ContextXmlPath = Join-Path $BaseDir "conf\Catalina\localhost\ROOT.xml"
$ContextDir = Split-Path $ContextXmlPath
if (-not (Test-Path $ContextDir)) {
    New-Item -ItemType Directory -Path $ContextDir -Force | Out-Null
}

$ContextContent = @"
<Context docBase="$WebappDir">
    <Resources>
        <PreResources className="org.apache.catalina.webresources.DirResourceSet"
                      base="$ClassesDir"
                      webAppMount="/WEB-INF/classes" />
    </Resources>
</Context>
"@

Set-Content -Path $ContextXmlPath -Value $ContextContent

# --- Tomcat 실행 ---
$Env:CATALINA_HOME = $TomcatHome
$Env:CATALINA_BASE = $BaseDir

# 현재 콘솔에서 시작하기 위해 catalina.bat run 사용
$CatalinaBat = Join-Path $TomcatHome "bin\catalina.bat"

Write-Host "Tomcat 시작 중..."
Write-Host "접속 URL: http://localhost:8080/"
& $CatalinaBat run
