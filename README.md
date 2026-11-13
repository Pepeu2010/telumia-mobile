# Telumia Mobile

![Telumia](https://raw.githubusercontent.com/Pepeu2010/telumia/main/assets/brand/telumia-banner.png)

**Seu cinema, suas séries e novelas — com foco no Brasil.** Cliente nativo Telumia para Android (APK), desenvolvido sobre a infraestrutura do Nuvio Mobile com Kotlin Multiplatform e Compose. Faz parte do projeto Telumia, junto de [Telumia TV](https://github.com/Pepeu2010/telumia-tv) e [Telumia Desktop](https://github.com/Pepeu2010/telumia-desktop).

## Estado

Pré-lançamento **0.6.12**: [baixe os APKs na página de releases](https://github.com/Pepeu2010/telumia-mobile/releases/latest). Para a maioria dos celulares, use `arm64-v8a`. A base é o [NuvioMedia/NuvioMobile](https://github.com/NuvioMedia/NuvioMobile) (`cmp-rewrite`, commit `e9f136b`), com identidade própria: nome **Telumia**, `applicationId` `io.github.pepeu2010.telumia.mobile`, abertura animada, português do Brasil por padrão e o visual do design system Telumia Mobile (Início, Detalhes, Busca, Biblioteca, Downloads, Configurações, Login e seleção de perfil).

Limitações: os APKs são assinados com a chave de debug do Android (não servem para a Play Store e só atualizam com a mesma chave), não são minificados e foram testados apenas em emulador. A aba Live TV é só interface, com canais de exemplo. O player ainda não foi validado com uma fonte de vídeo.

## Login

O app **sempre** usa o login do Nuvio original: mesmo backend e mesmo fluxo de conta, preservando contas, biblioteca e progresso. O módulo está em `composeApp/src/commonMain/kotlin/com/nuvio/app/core/auth/` e `core/network/SupabaseProvider.kt`; os links `nuvio://auth` foram mantidos de propósito. Não crie um sistema de contas paralelo nem altere o contrato de autenticação.

## Direção visual

Tema escuro, âmbar como acento, destaque em tela cheia, barra inferior flutuante e alvos de toque de 48dp. As telas e o design system estão no [projeto central](https://github.com/Pepeu2010/telumia) (`design-system/`).

## Compilar

Requer JDK 17 e Android SDK 36. Crie `local.properties` (ignorado pelo Git) com `sdk.dir`, `NUVIO_SUPABASE_URL` e `NUVIO_SUPABASE_ANON_KEY` (mesmo backend do Nuvio, também usado pelo Telumia TV). Não publique credenciais nem keystores.

```sh
./gradlew :androidApp:assembleFullDebug
```

## Sincronizar com o upstream

```sh
git fetch upstream cmp-rewrite
git merge upstream/cmp-rewrite
```

## Licença

[GPL-3.0](LICENSE). Copyrights, autoria e avisos de terceiros preservados. [Créditos do código](FORK_NOTICE.md).
