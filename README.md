# WinePC Android

Base de app Android para executar jogos Windows via Wine + Box64 em celulares ARM64.

## Requisitos
- Android 8.0+ (arm64-v8a)
- Runtime Wine/Box64 em `filesDir/runtime/`:
  - `bin/box64`
  - `bin/wine64`
  - `lib/` com as bibliotecas necessárias
  Use builds de projetos como Winlator ou Box64 como referência.

## Recursos
- Escolher um .exe (é copiado para o armazenamento interno do app)
- Permissão de acesso ao armazenamento (Android 11+: "Acesso a todos os arquivos")
- Tela cheia imersiva (alternável), com desktop virtual do Wine na resolução do aparelho
- Toggle de upscaling (estado; o shader ainda precisa ser implementado)

## Limitações
- Lossless Scaling não funciona no Android (é exclusivo de Windows)
- Upscaling real exige implementar shader (FSR/CAS) no GLSurfaceView
- Desempenho depende do SoC; jogos com anti-cheat ou DRM online podem não rodar
- `MANAGE_EXTERNAL_STORAGE` é recusado pela Google Play para a maioria dos apps

## Compilar
- Localmente: abra no Android Studio e rode `Build > Build APK(s)`
- No GitHub: a Action "Build APK" gera o APK em Actions > último run > Artifacts
