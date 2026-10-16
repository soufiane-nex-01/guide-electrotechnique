$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$compilerPath = Join-Path $env:WINDIR 'Microsoft.NET/Framework64/v4.0.30319/csc.exe'
if (-not (Test-Path -LiteralPath $compilerPath)) {
    $compilerPath = Join-Path $env:WINDIR 'Microsoft.NET/Framework/v4.0.30319/csc.exe'
}
if (-not (Test-Path -LiteralPath $compilerPath)) { throw 'Compilateur .NET Framework introuvable.' }
$sourcePath = Join-Path $projectRoot 'desktop/Source/LocalServer.cs'
$outputPath = Join-Path $projectRoot 'desktop/Guide-Electrotechnique.exe'
& $compilerPath /nologo /target:winexe /optimize+ /reference:System.Windows.Forms.dll /reference:System.Drawing.dll "/out:$outputPath" $sourcePath
if ($LASTEXITCODE -ne 0) { throw 'Compilation du lanceur Windows échouée.' }
Write-Output "Lanceur créé : $outputPath"
