package com.jim.emotionrecord.ui.theme

import androidx.compose.ui.graphics.Color

// Quest design tokens — from quest-tokens.css (locked, do not modify hex values)

val QBg          = Color(0xFFFBF7F2)
val QBgPaper     = Color(0xFFF7F1E6)
val QSurface     = Color(0xFFFFFFFF)
val QSurfaceSub  = Color(0xFFF2EBDE)
val QSurfaceSoft = Color(0xFFFAF3E7)

val QText1 = Color(0xFF3B2A20)
val QText2 = Color(0xFF7A6A5E)
val QText3 = Color(0xFFB5A89B)

val QPrimary     = Color(0xFFD85A30)
val QPrimaryDeep = Color(0xFFB84A24)
val QPrimarySoft = Color(0xFFFCE0D2)
val QPrimaryTint = Color(0x14D85A30)   // rgba(216,90,48,0.08)

val QLine       = Color(0x143B2A20)    // rgba(59,42,32,0.08)
val QLineStrong = Color(0x293B2A20)    // rgba(59,42,32,0.16)

// Emotion 5 colors — confirmed, locked
val QEmo1 = Color(0xFF626B66)   // 매우 나쁨
val QEmo2 = Color(0xFFFF9B7A)   // 나쁨
val QEmo3 = Color(0xFF63A16B)   // 보통
val QEmo4 = Color(0xFFB1CE86)   // 좋음
val QEmo5 = Color(0xFFE8D685)   // 매우 좋음

// Emotion tints
val QEmo1Tint = Color(0xFFE3E6E4)
val QEmo2Tint = Color(0xFFFFE3D5)
val QEmo3Tint = Color(0xFFDCEBDD)
val QEmo4Tint = Color(0xFFECF3DD)
val QEmo5Tint = Color(0xFFF7F0D2)

// Emotion inks — for strokes/text drawn on top of the emotion disc
val QEmo1Ink = Color(0xFF2C332E)
val QEmo2Ink = Color(0xFF7A2E16)
val QEmo3Ink = Color(0xFF1F3F22)
val QEmo4Ink = Color(0xFF3A521A)
val QEmo5Ink = Color(0xFF5A4814)

fun qEmoColor(level: Int): Color = when (level) {
    1 -> QEmo1; 2 -> QEmo2; 3 -> QEmo3; 4 -> QEmo4; else -> QEmo5
}

fun qEmoTint(level: Int): Color = when (level) {
    1 -> QEmo1Tint; 2 -> QEmo2Tint; 3 -> QEmo3Tint; 4 -> QEmo4Tint; else -> QEmo5Tint
}

fun qEmoInk(level: Int): Color = when (level) {
    1 -> QEmo1Ink; 2 -> QEmo2Ink; 3 -> QEmo3Ink; 4 -> QEmo4Ink; else -> QEmo5Ink
}
