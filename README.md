# Abrigo São Lázaro – App Android

<p align="center">
  <img src="projeto.gif" alt= "GIF da aplicação desenvolvida." />
</p> 

Projeto desenvolvido para o módulo intermediário de Android do [**Capacita iRede**](https://capacitabrasil.irede.org.br/).

Nome do aluno: Rodrigo Holanda Barbosa

Data de Entrega: 02/06/2026

Descrição: Aplicativo Android desenvolvido em **Kotlin + Jetpack Compose** para o
[Abrigo São Lázaro](https://abrigosaolazaro.org.br/), o maior abrigo de proteção
animal do Ceará, fundado em 1996 em Fortaleza. A ONG acolhe cerca de 1.200 cães e gatos resgatados de situações de abandono e maus-tratos.

Justificativa: Sou apoiador da causa animal e acho relevante o tema para a sociedade.

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
├── MainActivity.kt               # Main
│
├── data/
│   ├── db/
│   │   ├── AnimalEntity.kt       # Entidade Room com id, nome, idade, espécie, raça, foto, descrição
│   │   ├── AnimalDao.kt          # Operações de banco (Queries: Flow<List>, insertAll, count)
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
        │   ├── AdoptionViewModel.kt # StateFlow<List<AnimalEntity>> + Factory manual
        │   └── AdoptionScreen.kt  # Grid de animais (LazyVerticalGrid)
        └── contact/
            ├── ContactViewModel.kt # Estado do formulário com validação inline
            └── ContactScreen.kt   # Formulário adoção / denúncia + tela de sucesso
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

## Destaques de implementação:

- Tema laranja/marrom (#E8651A) fiel à identidade visual do abrigo, com suporte a dark mode.
- ShelterHeader.kt — carrega o logo real do site via Coil como aprendemos em aula, com ícone de patinha como fallback.

**OBS**: Foi corrigido o desalinhamento da imagem do logo da ONG no CircleShape da Header, ajustando *alignment = Alignment.CenterStart* na AsyncImage para que foque no brasão do abrigo. (Não se basear pelo GIF)
  
---

## Como importar no Android Studio

1. Abra o **Android Studio** (Hedgehog ou superior).
2. `File → Open` → selecione a pasta **AbrigoSaoLazaro**.
3. Aguarde o **Gradle Sync** (baixa dependências automaticamente).
4. Conecte um dispositivo/emulador e clique em **Run ▶**.

---

## Requisitos mínimos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK: minSdk **24**, targetSdk **34**
- Conexão com internet (para carregar fotos e o logo do abrigo via Coil)
