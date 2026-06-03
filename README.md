# Abrigo São Lázaro – App Android

Aplicativo Android desenvolvido em **Kotlin + Jetpack Compose** para o
[Abrigo São Lázaro](https://abrigosaolazaro.org.br/), o maior abrigo de proteção
animal do Ceará, fundado em 1996 em Fortaleza.

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| UI | Jetpack Compose + Material 3 |
| Navegação | Navigation Compose |
| Banco de dados | Room (SQLite) |
| Arquitetura | MVVM (Model-View-ViewModel) |
| Imagens | Coil Compose |
| Build | Gradle KTS + KSP |

---

## Estrutura do projeto

```
app/src/main/java/br/com/abrigosaolazaro/
├── AbrigoApplication.kt          # Application class (inicializa Room)
├── MainActivity.kt               # Entry-point (enableEdgeToEdge)
│
├── data/
│   ├── db/
│   │   ├── AnimalEntity.kt       # Entidade Room
│   │   ├── AnimalDao.kt          # Operações de banco (Flow + suspend)
│   │   └── AppDatabase.kt        # Singleton do banco Room
│   └── repository/
│       └── AnimalRepository.kt   # Repositório + seed de dados iniciais
│
└── ui/
    ├── theme/
    │   ├── Color.kt              # Paleta laranja/marrom do abrigo
    │   ├── Type.kt               # Tipografia
    │   └── Theme.kt              # Light + Dark theme
    ├── navigation/
    │   └── NavGraph.kt           # Rotas e NavHost
    ├── components/
    │   └── ShelterHeader.kt      # Cabeçalho compartilhado (logo + nome)
    └── screens/
        ├── adoption/
        │   ├── AdoptionViewModel.kt
        │   └── AdoptionScreen.kt  # Grid de animais (LazyVerticalGrid)
        └── contact/
            ├── ContactViewModel.kt
            └── ContactScreen.kt   # Formulário adoção / denúncia
```

---

## Telas

### 1. Quero Adotar (`AdoptionScreen`)
- Grid 2 colunas com os animais cadastrados no Room
- Cada card: foto (Coil), nome, espécie, idade, botão **Adotar**
- Botão flutuante "Contato / Denúncia" → navega para a Tela 2

### 2. Contato & Denúncia (`ContactScreen`)
- Seletor de tipo: **Adoção** ou **Denúncia de maus-tratos**
- Quando vindo do botão Adotar, o animal é pré-preenchido
- Campos: animal, nome, e-mail, telefone, mensagem
- Validação inline + tela de sucesso ao enviar

---

## Como importar no Android Studio

1. Abra o **Android Studio** (Hedgehog ou superior).
2. `File → Open` → selecione a pasta **AbrigoSaoLazaro**.
3. Aguarde o **Gradle Sync** (baixa dependências automaticamente).
4. Conecte um dispositivo/emulador e clique em **Run ▶**.

> **Nota:** o arquivo `gradle-wrapper.jar` será baixado pelo Android Studio
> automaticamente na primeira sincronização.

---

## Requisitos mínimos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK: minSdk **24**, targetSdk **34**
- Conexão com internet (para carregar fotos e o logo do abrigo via Coil)
