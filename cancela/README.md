# 🔒 Cancela — modo criança para Android

Deixe seu filho usar o celular **só com os apps que você liberar**. Você escolhe os apps e controla tudo com a **sua digital**.

[![Build](../../actions/workflows/build.yml/badge.svg)](../../actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

> 🇬🇧 *English summary at the bottom.*

## O que ele faz

- **Tela da criança:** grade com ícones grandes, só dos apps liberados.
- **Bloqueio dos demais apps:** qualquer app fora da lista é jogado de volta para a grade.
- **Painel dos pais:** só abre com biometria (digital/rosto). Lá você liga/desliga o modo e marca os apps.
- **Widget** na tela inicial e **atalho nas configurações rápidas:** um toque liga o modo criança; **desligar exige biometria**.
- **100% local:** sem internet, sem anúncios, sem coleta de dados ([privacidade](PRIVACY.md)).

## Como funciona

Um *serviço de acessibilidade* observa qual app veio para a frente. No modo criança, se o app não estiver na lista de liberados, o Cancela abre a própria grade por cima. A biometria usa o `BiometricPrompt` do Android.

## Instalação (usuário)

1. Baixe o APK mais recente em **[Releases](../../releases)**.
2. Abra o arquivo e permita *instalar apps desconhecidos* para o app que o abriu.
   Se o Play Protect avisar, toque em **Mais detalhes → Instalar mesmo assim**.
3. **Android 13+:** vá em *Configurações → Apps → Cancela → ⋮ → Permitir configurações restritas*.
4. Ative em *Configurações → Acessibilidade → Apps instalados → Cancela*.
5. Em *Configurações → Apps → Cancela → Bateria*, escolha **Sem restrições** (evita o sistema matar o serviço).
6. Abra o Cancela → **🔒 Pais** (digital) e siga os botões: tela inicial, atalho rápido e lista de apps.
7. Ligue o **Modo criança**.

> Requer Android 8.0+ e uma biometria cadastrada no aparelho. Desenvolvido pensando no Moto G73; ainda sem testes amplos em outros aparelhos.

## Compilar do código

Requisitos: JDK 17 e Android SDK 34 (ou apenas o Android Studio).

```bash
git clone https://github.com/<seu-usuario>/cancela.git
cd cancela
./gradlew assembleDebug
# APK em app/build/outputs/apk/debug/app-debug.apk
```

Ou abra a pasta no **Android Studio** e clique em ▶ Run.

## Publicar uma versão (APK assinado pelo GitHub Actions)

1. Gere uma chave (uma vez): `keytool -genkey -v -keystore release.jks -alias cancela -keyalg RSA -keysize 2048 -validity 9125`
2. **Guarde `release.jks` e as senhas em backup** — sem elas não dá para atualizar o app depois.
3. Converta para texto: `base64 -w0 release.jks > keystore.b64` (no Mac: `base64 -i release.jks`).
4. No GitHub: *Settings → Secrets and variables → Actions* e crie:
   `KEYSTORE_BASE64` (conteúdo do `keystore.b64`), `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
5. Aumente `versionCode`/`versionName` em `app/build.gradle.kts`, faça commit e crie a tag:
   ```bash
   git tag v1.0.0 && git push origin v1.0.0
   ```
6. O workflow **Release** gera `Cancela-v1.0.0.apk` e publica em *Releases*.

> Não suba `release.jks` nem senhas para o repositório (o `.gitignore` já ignora `*.jks`).

## Limitações conhecidas

- O app protegido pode aparecer por uma fração de segundo antes do redirecionamento (limitação do Android).
- A criança ainda pode puxar a barra de notificações. Para travar, use a **Fixação de apps** do Android.
- Não impede desinstalar o Cancela nem desativar a acessibilidade com o celular desbloqueado por um adulto.
- Em alguns fabricantes (Xiaomi, Samsung etc.) é preciso liberar bateria/início automático.
- Não é uma ferramenta de segurança certificada e **não substitui a supervisão de um adulto**.

## Ideias / Roadmap

- [ ] Limite de tempo de uso por app
- [ ] Botão "pausar tudo"
- [ ] Ocultar/bloquear a barra de notificações (requer *device owner*)
- [ ] Traduções

## Contribuindo

Issues e PRs são bem-vindos. Ao reportar um problema, informe **modelo do celular e versão do Android**.

## Distribuição e verificação de desenvolvedor do Google

O Google vem exigindo que apps instalados em aparelhos certificados venham de desenvolvedores verificados, começando por Brasil, Indonésia, Singapura e Tailândia, com expansão global prevista para 2027. Se for distribuir o APK por fora da Play Store, veja as regras atuais em <https://developer.android.com/developer-verification>. Usuários também podem instalar apps não verificados pelo fluxo avançado do Android ou via ADB.

## Licença

[MIT](LICENSE)

---

## 🇬🇧 English summary

**Cancela** is an Android kid-mode launcher: pick which apps a toddler can open, protected by your fingerprint. An accessibility service bounces any non-allowed app back to a big-icon grid; the parent panel, widget and Quick Settings tile all require biometrics to turn the mode off. 100% local, no internet permission. Requires Android 8+. Build with `./gradlew assembleDebug`. MIT licensed.
