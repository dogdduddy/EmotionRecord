package com.jim.emotionrecord.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Surfaces ────────────────────────────────────────────────────────────────
val Bg          = Color(0xFFFBF7F2)   // 앱 배경 (크림)
val BgWarm      = Color(0xFFF8F1E8)
val EmoSurface  = Color(0xFFFFFFFF)   // 카드 표면
val SurfaceSub  = Color(0xFFF5EFE7)   // 보조 표면
val SurfaceSoft = Color(0xFFFBF5ED)   // 빈 상태 등

// ─── Text ─────────────────────────────────────────────────────────────────────
val Text1 = Color(0xFF3E2C23)  // 타이틀
val Text2 = Color(0xFF7D6B5F)  // 본문
val Text3 = Color(0xFFB5A89C)  // 보조/캡션

// ─── Primary (coral) ──────────────────────────────────────────────────────────
val Primary      = Color(0xFFFF8A65)
val PrimaryDeep  = Color(0xFFE07856)
val PrimarySoft  = Color(0xFFFFE3D4)
val PrimaryTint  = Color(0x14FF8A65)   // alpha 8%

// ─── Lines ─────────────────────────────────────────────────────────────────────
val Line       = Color(0x143E2C23)    // alpha 8%
val LineStrong = Color(0x293E2C23)    // alpha 16%

// ─── Emotion scale (softened saturation) ────────────────────────────────────
val Emo1 = Color(0xFF7986CB)   // 매우 나쁨 — indigo
val Emo2 = Color(0xFF9F8FCB)   // 나쁨 — lavender
val Emo3 = Color(0xFFB0AAA3)   // 보통 — warm gray
val Emo4 = Color(0xFFF2B66D)   // 좋음 — warm yellow
val Emo5 = Color(0xFFFF9B7A)   // 매우 좋음 — coral

// Emotion tint backgrounds
val Emo1Tint = Color(0xFFE8EAF6)
val Emo2Tint = Color(0xFFEDE7F5)
val Emo3Tint = Color(0xFFEFECE8)
val Emo4Tint = Color(0xFFFCEFDC)
val Emo5Tint = Color(0xFFFFE7DC)

// Emotion ink (dark, for text on tint)
val Emo1Ink = Color(0xFF3D4783)
val Emo2Ink = Color(0xFF574B85)
val Emo3Ink = Color(0xFF5C544B)
val Emo4Ink = Color(0xFF8C5E1F)
val Emo5Ink = Color(0xFFA14A2A)

// ─── Helpers ─────────────────────────────────────────────────────────────────
fun emotionColor(score: Int): Color = when (score) {
    1 -> Emo1; 2 -> Emo2; 3 -> Emo3; 4 -> Emo4; 5 -> Emo5
    else -> Emo3
}

fun emotionTint(score: Int): Color = when (score) {
    1 -> Emo1Tint; 2 -> Emo2Tint; 3 -> Emo3Tint; 4 -> Emo4Tint; 5 -> Emo5Tint
    else -> Emo3Tint
}

fun emotionInk(score: Int): Color = when (score) {
    1 -> Emo1Ink; 2 -> Emo2Ink; 3 -> Emo3Ink; 4 -> Emo4Ink; 5 -> Emo5Ink
    else -> Emo3Ink
}
