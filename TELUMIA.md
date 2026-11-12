# Telumia Mobile

Cliente nativo Telumia para Android (APK), com interface cinematográfica e foco no Brasil.
Faz parte do projeto Telumia, junto de [telumia-tv](https://github.com/Pepeu2010/telumia-tv) e
[telumia-desktop](https://github.com/Pepeu2010/telumia-desktop).

## Origem

Este repositório começou como uma cópia de [NuvioMedia/NuvioMobile](https://github.com/NuvioMedia/NuvioMobile)
(branch `cmp-rewrite`, commit `e9f136bc7cc1694be1413db0dba61292bd3eb047`, versão 0.6.12-beta), licenciado sob
GPL-3.0. Telumia é um fork de desenvolvimento independente e não é um cliente oficial do Nuvio; o nome não
sugere endosso da NuvioMedia. Veja `NOTICE.md`.

## Regra de login

O app **sempre** usa o login do Nuvio original: mesmo backend e mesmo fluxo de conta
(`composeApp/src/commonMain/kotlin/com/nuvio/app/core/auth/`, `core/network/SupabaseProvider.kt`).
Não criar um sistema de contas paralelo nem alterar o contrato de autenticação. Mudanças nesse módulo devem
manter a compatibilidade com o backend do Nuvio.

## Direção visual

Segue o sistema de design Telumia Mobile (tema escuro, âmbar como acento, destaque em tela cheia, barra
inferior flutuante, alvos de toque de 48dp). O desenho das telas está no repositório principal
[Pepeu2010/telumia](https://github.com/Pepeu2010/telumia) (`design-system/`).

## Sincronizar com o upstream

```bash
git fetch upstream cmp-rewrite
git merge upstream/cmp-rewrite
```

## Estado

Fork do NuvioMobile; base sem alterações de código além destes avisos. Nome do app, `applicationId`, ícones e tema ainda são os do Nuvio.
