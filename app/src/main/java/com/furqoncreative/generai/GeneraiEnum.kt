package com.furqoncreative.generai

enum class Tone(val prompt: String) {
    PROFESSIONAL("professional"),
    CASUAL("casual"),
    FUNNY("funny"),
    INFORMATIVE("informative")
}

enum class Format(val prompt: String) {
    PARAGRAPH("paragraph"),
    EMAIL("email draft"),
    IDEAS("list of ideas"),
    BLOG_POST("blog post")
}

enum class Length(val prompt: String) {
    SHORT("short"),
    MEDIUM("medium"),
    LONG("long"),
}