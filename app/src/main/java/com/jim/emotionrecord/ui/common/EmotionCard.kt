package com.jim.emotionrecord.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jim.emotionrecord.domain.model.EmotionCheck
import com.jim.emotionrecord.ui.theme.Line
import com.jim.emotionrecord.ui.theme.Primary
import com.jim.emotionrecord.ui.theme.Text2
import com.jim.emotionrecord.ui.theme.Text3
import com.jim.emotionrecord.ui.theme.emotionInk
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("a h:mm")
private val cardShape = RoundedCornerShape(18.dp)

@Composable
fun EmotionCard(
    record: EmotionCheck,
    onEditMemoClick: ((EmotionCheck) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isEditable = record.isMemoEditable && onEditMemoClick != null
    val border = if (isEditable) {
        BorderStroke(1.5.dp, Primary)
    } else {
        BorderStroke(1.dp, Line)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        border = border,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEditable) 0.dp else 1.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = isEditable) { onEditMemoClick?.invoke(record) }
                .padding(14.dp)
        ) {
            // 좌측: FaceChip 44dp
            FaceChip(score = record.emotion.score, size = 44.dp)

            Spacer(Modifier.width(12.dp))

            // 중앙: 감정 라벨 + 시간 + 메모
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = record.emotion.label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = emotionInk(record.emotion.score)
                    )
                    Text(
                        text = record.recordedAt
                            .atZone(ZoneId.systemDefault())
                            .format(timeFormatter),
                        style = MaterialTheme.typography.labelMedium,
                        color = Text3
                    )
                }
                if (record.memo.isNotBlank()) {
                    Text(
                        text = record.memo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Text2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 우측: 수정 가능한 경우 연필 아이콘
            if (isEditable) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "메모 편집",
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
