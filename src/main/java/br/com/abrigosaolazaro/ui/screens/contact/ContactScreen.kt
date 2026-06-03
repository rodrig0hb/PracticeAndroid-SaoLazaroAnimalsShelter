package br.com.abrigosaolazaro.ui.screens.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.abrigosaolazaro.ui.components.ShelterHeader
import br.com.abrigosaolazaro.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    viewModel           : ContactViewModel,
    prefilledAnimalName : String = "",
    onBackClick         : () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Pré-preenche o nome do animal ao abrir a tela via botão Adotar
    LaunchedEffect(prefilledAnimalName) {
        viewModel.setPrefilledAnimalName(prefilledAnimalName)
    }

    Scaffold(
        topBar = {
            Column {
                ShelterHeader()
                TopAppBar(
                    title = {
                        Text(
                            text = if (state.contactType == ContactType.ADOPTION)
                                "Formulário de Adoção" else "Denúncia de Maus-Tratos",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor           = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor        = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        if (state.isSubmitted) {
            SuccessContent(
                modifier = Modifier.padding(innerPadding),
                onBack   = { viewModel.resetForm(); onBackClick() }
            )
        } else {
            ContactFormContent(
                state               = state,
                modifier            = Modifier.padding(innerPadding),
                onNameChange        = viewModel::onNameChange,
                onEmailChange       = viewModel::onEmailChange,
                onPhoneChange       = viewModel::onPhoneChange,
                onAnimalNameChange  = viewModel::onAnimalNameChange,
                onMessageChange     = viewModel::onMessageChange,
                onContactTypeChange = viewModel::onContactTypeChange,
                onSubmit            = viewModel::submit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactFormContent(
    state               : ContactFormState,
    modifier            : Modifier = Modifier,
    onNameChange        : (String) -> Unit,
    onEmailChange       : (String) -> Unit,
    onPhoneChange       : (String) -> Unit,
    onAnimalNameChange  : (String) -> Unit,
    onMessageChange     : (String) -> Unit,
    onContactTypeChange : (ContactType) -> Unit,
    onSubmit            : () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Seletor de tipo ──────────────────────────────────────────
        Text("Tipo de solicitação", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(
                selected = state.contactType == ContactType.ADOPTION,
                onClick  = { onContactTypeChange(ContactType.ADOPTION) },
                label    = { Text("🐾  Adoção") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = state.contactType == ContactType.ABUSE_REPORT,
                onClick  = { onContactTypeChange(ContactType.ABUSE_REPORT) },
                label    = { Text("⚠️  Denúncia") },
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Animal (somente para adoção) ─────────────────────────────
        if (state.contactType == ContactType.ADOPTION) {
            OutlinedTextField(
                value         = state.animalName,
                onValueChange = onAnimalNameChange,
                label         = { Text("Animal de interesse") },
                leadingIcon   = { Icon(Icons.Default.Pets, contentDescription = null) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true
            )
        }

        // ── Nome ─────────────────────────────────────────────────────
        OutlinedTextField(
            value         = state.name,
            onValueChange = onNameChange,
            label         = { Text("Nome completo *") },
            leadingIcon   = { Icon(Icons.Default.Person, contentDescription = null) },
            isError       = state.nameError != null,
            supportingText = state.nameError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            singleLine    = true
        )

        // ── E-mail ───────────────────────────────────────────────────
        OutlinedTextField(
            value          = state.email,
            onValueChange  = onEmailChange,
            label          = { Text("E-mail *") },
            leadingIcon    = { Icon(Icons.Default.Email, contentDescription = null) },
            isError        = state.emailError != null,
            supportingText = state.emailError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier       = Modifier.fillMaxWidth(),
            shape          = RoundedCornerShape(12.dp),
            singleLine     = true
        )

        // ── Telefone ─────────────────────────────────────────────────
        OutlinedTextField(
            value           = state.phone,
            onValueChange   = onPhoneChange,
            label           = { Text("Telefone / WhatsApp") },
            leadingIcon     = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier        = Modifier.fillMaxWidth(),
            shape           = RoundedCornerShape(12.dp),
            singleLine      = true
        )

        // ── Mensagem ─────────────────────────────────────────────────
        OutlinedTextField(
            value         = state.message,
            onValueChange = onMessageChange,
            label = {
                Text(
                    if (state.contactType == ContactType.ADOPTION)
                        "Conte sobre você e seu lar *"
                    else
                        "Descreva a situação de maus-tratos *"
                )
            },
            isError        = state.messageError != null,
            supportingText = state.messageError?.let { err -> { Text(err, color = MaterialTheme.colorScheme.error) } },
            modifier       = Modifier.fillMaxWidth().height(140.dp),
            shape          = RoundedCornerShape(12.dp),
            maxLines       = 6
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Botão Enviar ─────────────────────────────────────────────
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.contactType == ContactType.ADOPTION)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = if (state.contactType == ContactType.ADOPTION)
                    Icons.Default.Favorite else Icons.Default.Flag,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.contactType == ContactType.ADOPTION)
                    "Enviar pedido de adoção" else "Registrar denúncia",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Text(
            text      = "* Campos obrigatórios",
            style     = MaterialTheme.typography.labelSmall,
            color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SuccessContent(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Enviado com sucesso",
            modifier = Modifier.size(96.dp),
            tint = SuccessGreen
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text      = "Mensagem enviada!",
            style     = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color     = SuccessGreen,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text      = "Obrigado pelo seu contato.\nEm breve nossa equipe entrará em contato.",
            style     = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedButton(
            onClick  = onBack,
            modifier = Modifier.fillMaxWidth(0.65f).height(50.dp),
            shape    = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Voltar ao início")
        }
    }
}
