package com.jim.emotionrecord.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Pretendard 없으면 시스템 sans-serif fallback
private val QuestFont = FontFamily.SansSerif

val QuestTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(800),
        fontSize = 28.sp,
        lineHeight = 33.6.sp,
        letterSpacing = (-0.02).em
    ),
    headlineMedium = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(800),
        fontSize = 24.sp,
        lineHeight = 31.2.sp,
        letterSpacing = (-0.02).em
    ),
    headlineSmall = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(800),
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.02).em
    ),
    titleLarge = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(700),
        fontSize = 17.sp,
        lineHeight = 22.1.sp,
        letterSpacing = (-0.01).em
    ),
    titleMedium = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(700),
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = (-0.01).em
    ),
    titleSmall = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        letterSpacing = (-0.01).em
    ),
    bodyLarge = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(400),
        fontSize = 14.sp,
        lineHeight = 21.7.sp,
        letterSpacing = (-0.01).em
    ),
    bodyMedium = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(400),
        fontSize = 13.sp,
        lineHeight = 19.5.sp,
        letterSpacing = (-0.01).em
    ),
    bodySmall = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(400),
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = (-0.01).em
    ),
    labelLarge = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(700),
        fontSize = 16.sp,
        lineHeight = 20.8.sp,
        letterSpacing = (-0.01).em
    ),
    labelMedium = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        letterSpacing = (-0.01).em
    ),
    labelSmall = TextStyle(
        fontFamily = QuestFont,
        fontWeight = FontWeight(600),
        fontSize = 11.sp,
        lineHeight = 15.4.sp,
        letterSpacing = (-0.01).em
    ),
)
