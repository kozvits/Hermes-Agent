package com.nousresearch.hermesagent.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nousresearch.hermesagent.ui.theme.HermesStatusColors
import com.nousresearch.hermesagent.ui.theme.Dimens

// ── Connection Badge ──
@Composable
fun ConnectionBadge(
    status: String, // "connected", "disconnected", "connecting", "error"
    modifier: Modifier = Modifier,
) {
    val color = when (status) {
        "connected" -> HermesStatusColors.connected
        "connecting" -> HermesStatusColors.thinking
        "error" -> HermesStatusColors.error
        else -> HermesStatusColors.disconnected
    }
    val label = when (status) {
        "connected" -> "Подключено"
        "connecting" -> "Подключение…"
        "error" -> "Ошибка"
        else -> "Отключено"
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Loading Indicator (Thinking animation) ──
@Composable
fun ThinkingIndicator(modifier: Modifier = Modifier) {
    val dots = listOf("●", "●", "●")
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        modifier = modifier.padding(Dimens.padding_sm),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Думает",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        dots.forEachIndexed { index, dot ->
            val delay = index * 300
            val animatedAlpha: Float by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable<Float>(
                    animation = tween<Float>(
                        durationMillis = 900,
                        delayMillis = delay,
                    )
                ),
            )
            Text(
                text = dot,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = animatedAlpha),
            )
        }
    }
}

// ── Tool Execution Card ──
@Composable
fun ToolExecutionCard(
    toolName: String,
    status: String,
    output: String = "",
    modifier: Modifier = Modifier,
) {
    val bgColor = when (status) {
        "running" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        "completed" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        "error" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.message_border_radius),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(Dimens.padding_sm)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                val icon = if (status == "running") "⚡" else if (status == "completed") "✅" else "❌"
                Text(text = icon, fontSize = 12.sp)
                Text(
                    text = toolName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (status == "running") {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (output.isNotBlank()) {
                Text(
                    text = output,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

// ── Markdown Message Bubble ──
@Composable
fun MessageBubble(
    content: String,
    role: String,
    modifier: Modifier = Modifier,
) {
    val isUser = role == "user"
    val isTool = role == "tool"

    val bgColor = when {
        isUser -> MaterialTheme.colorScheme.primary
        isTool -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        isUser -> MaterialTheme.colorScheme.onPrimary
        isTool -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val shape = if (isUser) {
        RoundedCornerShape(
            topStart = Dimens.message_border_radius,
            topEnd = 4.dp,
            bottomStart = Dimens.message_border_radius,
            bottomEnd = Dimens.message_border_radius,
        )
    } else {
        RoundedCornerShape(
            topStart = 4.dp,
            topEnd = Dimens.message_border_radius,
            bottomStart = Dimens.message_border_radius,
            bottomEnd = Dimens.message_border_radius,
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(Dimens.avatar_size)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "H",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
            Text(
                text = if (isUser) "Вы" else "Hermes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!isUser) {
                val modelLabel = "deepseek-v4-flash-free"
                Text(
                    text = modelLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                )
            }
        }

        Surface(
            shape = shape,
            color = bgColor,
            shadowElevation = if (isUser) 0.dp else 0.dp,
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.padding(
                    horizontal = Dimens.padding_lg,
                    vertical = Dimens.padding_sm,
                ),
            )
        }
    }
}

// ── Empty State ──
@Composable
fun EmptyState(
    title: String = "Нет сообщений",
    subtitle: String = "Начните разговор с Hermes!",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.padding_xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🤖",
            fontSize = 64.sp,
        )
        Spacer(modifier = Modifier.height(Dimens.padding_lg))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Dimens.padding_sm))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Section Header ──
@Composable
fun SectionHeader(
    title: String,
    action: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.padding_lg, vertical = Dimens.padding_sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        action?.invoke()
    }
}

// ── Stat Card ──
@Composable
fun StatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.padding_md)
                .defaultMinSize(minHeight = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
