package com.jim.emotionrecord.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Pretendard는 res/font/에 추가 시 FontFamily를 교체하세요.
// 현재는 시스템 기본 sans-serif (SansSerif) 사용.
private val AppFont = FontFamily.SansSerif

val Typography = Typography(
    // Display — 26sp / 800
    displayLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight(800),
        fontSize = 26.sp,
        lineHeight = (26 * 1.2).sp,
        letterSpacing = (-0.02).em
    ),
    // Title — 22sp / 700
    titleLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = (22 * 1.25).sp,
        letterSpacing = (-0.02).em
    ),
    // Section — 17sp / 700
    titleMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = (17 * 1.3).sp,
        letterSpacing = (-0.01).em
    ),
    // Subhead — 14sp / 600
    titleSmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight(600),
        fontSize = 14.sp,
        lineHeight = (14 * 1.4).sp,
        letterSpacing = (-0.01).em
    ),
    // Body — 13sp / 400
    bodyMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = (13 * 1.5).sp,
        letterSpacing = (-0.01).em
    ),
    // Body large — 15sp / 700 (emotion label in card)
    bodyLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = (15 * 1.4).sp,
        letterSpacing = (-0.01).em
    ),
    // Caption — 11sp / 500
    labelSmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight(500),
        fontSize = 11.sp,
        lineHeight = (11 * 1.4).sp,
        letterSpacing = 0.em
    ),
    // Label medium — 12sp / 500 (time, sub labels)
    labelMedium = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = (12 * 1.4).sp,
        letterSpacing = (-0.01).em
    ),
    // Label large — 16sp / 700 (button)
    labelLarge = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = (16 * 1.3).sp,
        letterSpacing = (-0.01).em
    ),
    bodySmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = (12 * 1.5).sp,
        letterSpacing = (-0.01).em
    ),
    headlineSmall = TextStyle(
        fontFamily = AppFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = (22 * 1.25).sp,
        letterSpacing = (-0.02).em
    )
)
