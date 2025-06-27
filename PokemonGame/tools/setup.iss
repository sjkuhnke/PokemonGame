[Setup]
AppName=Pokemon Xhenos
AppVersion=1.0
DefaultDirName={pf}\Pokemon Xhenos
DefaultGroupName=Pokemon Xhenos
OutputBaseFilename=PokemonXhenosInstaller
Compression=lzma
SolidCompression=yes
DisableProgramGroupPage=yes
AllowNoIcons=yes
UninstallDisplayIcon={app}\Pokemon_Xhenos.exe
SetupIconFile=build\favicon.ico
WizardStyle=modern
PrivilegesRequired=admin

[Files]
Source: "prod\Pokemon_Xhenos.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist\\PokemonXhenos-v0.0.4.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "build\jre\*"; DestDir: "{app}\jre"; Flags: recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos.exe"
Name: "{userdesktop}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos.exe"; Tasks: desktopicon

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Additional icons:"

[Run]
Filename: "{app}\Pokemon_Xhenos.exe"; Description: "Launch Pokemon Xhenos"; Flags: nowait postinstall skipifsilent
