# Define Maven version and installation paths
$MAVEN_VERSION = "3.9.6"
$MAVEN_URL = "https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"
$INSTALL_DIR = "C:\Program Files\Maven"
$MAVEN_HOME = "$INSTALL_DIR\apache-maven-$MAVEN_VERSION"
$ZIP_FILE = "$env:TEMP\maven.zip"

Write-Host "Installing Maven $MAVEN_VERSION..."

# Create installation directory if it doesn't exist
if (!(Test-Path $INSTALL_DIR)) {
    New-Item -ItemType Directory -Path $INSTALL_DIR | Out-Null
}

# Download Maven
Write-Host "Downloading Maven..."
Invoke-WebRequest -Uri $MAVEN_URL -OutFile $ZIP_FILE

# Extract Maven
Write-Host "Extracting Maven..."
Expand-Archive -Path $ZIP_FILE -DestinationPath $INSTALL_DIR -Force

# Clean up zip file
Remove-Item $ZIP_FILE

# Set environment variables
Write-Host "Setting environment variables..."
$userPath = [Environment]::GetEnvironmentVariable("Path", "User")

# Remove old Maven paths if they exist
$userPath = ($userPath.Split(';') | Where-Object { $_ -notlike "*Maven*" }) -join ';'

# Add new Maven path
$userPath = "$userPath;$MAVEN_HOME\bin"
[Environment]::SetEnvironmentVariable("Path", $userPath, "User")
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $MAVEN_HOME, "User")

Write-Host "Maven installation completed!"
Write-Host "Please restart your terminal for the changes to take effect."
Write-Host "You can verify the installation by running: mvn -version" 