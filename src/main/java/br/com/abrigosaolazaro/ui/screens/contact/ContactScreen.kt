package br.com.abrigosaolazaro.ui.screens.contact

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onBackClick         : () -> Unit,
    onLocationClick     : () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                            if (state.contactType == ContactType.ADOPTION)
                                "Formulário de Adoção" else "Denúncia de Maus-Tratos",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Voltar")
                        }
                    },
                    actions = {
                        IconButton(onClick = onLocationClick) {
                            Icon(Icons.Default.LocationOn, "Ver Localização")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor            = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor         = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor    = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    ) { padding ->
        if (state.isSubmitted) {
            SuccessContent(modifier = Modifier.padding(padding), onBack = { viewModel.resetForm(); onBackClick() })
        } else {
            FormContent(
                state               = state,
                modifier            = Modifier.padding(padding),
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
private fun FormContent(
    state               : ContactFormState,
    modifier            : Modifier,
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
        // Type selector
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

        HorizontalDivider()

        if (state.contactType == ContactType.ADOPTION) {
            OutlinedTextField(
                value = state.animalName, onValueChange = onAnimalNameChange,
                label = { Text("Animal de interesse") },
                leadingIcon = { Icon(Icons.Default.Pets, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
            )
        }

        OutlinedTextField(
            value = state.name, onValueChange = onNameChange,
            label = { Text("Nome completo *") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            isError = state.nameError != null,
            supportingText = state.nameError?.let { e -> { Text(e, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )

        OutlinedTextField(
            value = state.email, onValueChange = onEmailChange,
            label = { Text("E-mail *") },
            leadingIcon = { Icon(Icons.Default.Email, null) },
            isError = state.emailError != null,
            supportingText = state.emailError?.let { e -> { Text(e, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )

        OutlinedTextField(
            value = state.phone, onValueChange = onPhoneChange,
            label = { Text("Telefone / WhatsApp") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
        )

        OutlinedTextField(
            value = state.message, onValueChange = onMessageChange,
            label = {
                Text(if (state.contactType == ContactType.ADOPTION)
                    "Conte sobre você e seu lar *" else "Descreva a situação *")
            },
            isError = state.messageError != null,
            supportingText = state.messageError?.let { e -> { Text(e, color = MaterialTheme.colorScheme.error) } },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            shape = RoundedCornerShape(12.dp), maxLines = 6
        )

        Button(
            onClick  = onSubmit,
            enabled  = !state.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (state.contactType == ContactType.ADOPTION)
                    MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(if (state.contactType == ContactType.ADOPTION) Icons.Default.Favorite else Icons.Default.Flag, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (state.contactType == ContactType.ADOPTION) "Enviar pedido de adoção" else "Registrar denúncia",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Text("* Campos obrigatórios", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SuccessContent(modifier: Modifier, onBack: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(96.dp), tint = SuccessGreen)
        Spacer(Modifier.height(24.dp))
        Text("Mensagem enviada!", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold, color = SuccessGreen, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text("Obrigado! Em breve nossa equipe entrará em contato.",
            style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(40.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth(0.65f).height(50.dp),
            shape = RoundedCornerShape(12.dp)) {
            Icon(Icons.Default.ArrowBack, null)
            Spacer(Modifier.width(8.dp))
            Text("Voltar ao início")
        }
    }
}
