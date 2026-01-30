[Setup]
AppName=Pokemon Xhenos
AppVersion=0.8.79
VersionInfoVersion=0.8.79
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
Source: "prod\Pokemon_Xhenos_console.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist\\PokemonXhenos-v0.8.79.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "build\jre\*"; DestDir: "{app}\jre"; Flags: recursesubdirs createallsubdirs

[Icons]
; Start Menu
Name: "{group}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos.exe"; Tasks: not debugconsole
Name: "{group}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos_console.exe"; Tasks: debugconsole

; Desktop
Name: "{userdesktop}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos.exe"; Tasks: desktopicon and not debugconsole
Name: "{userdesktop}\Pokemon Xhenos"; Filename: "{app}\Pokemon_Xhenos_console.exe"; Tasks: desktopicon and debugconsole

[Tasks]
Name: "desktopicon"; Description: "Create a &desktop shortcut"; GroupDescription: "Additional icons:"
Name: "debugconsole"; Description: "Run with debug console"; Flags: unchecked

[Run]
Filename: "{app}\Pokemon_Xhenos.exe"; \
    Description: "Launch Pokemon Xhenos"; \
    Tasks: not debugconsole; \
    Flags: nowait postinstall skipifsilent

[Run]
Filename: "{app}\Pokemon_Xhenos_console.exe"; \
    Description: "Launch Pokemon Xhenos (Console Mode)"; \
    Tasks: debugconsole; \
    Flags: nowait postinstall skipifsilent

[InstallDelete]
Type: filesandordirs; Name: "{app}"
