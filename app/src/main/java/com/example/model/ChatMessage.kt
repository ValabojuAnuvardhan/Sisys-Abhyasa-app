package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enumeration representing the sender type for a chat message.
 */
enum class SenderType {
    USER,
    AI
}

/**
 * Kotlin data class representing a ChatMessage containing sender type (USER/AI),
 * message content, and a timestamp, along with optional metadata for context and trust badges.
 */
data class ChatMessage(
    val id: String = "msg_${System.currentTimeMillis()}",
    val senderType: SenderType = SenderType.USER,
    val sender: String = if (senderType == SenderType.USER) "user" else "ai",
    val content: String,
    val timestamp: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val contextTag: String? = null,
    val trustBadge: String? = null
) {
    /**
     * Primary constructor accepting the required senderType, content, and timestamp.
     */
    constructor(
        senderType: SenderType,
        content: String,
        timestamp: String
    ) : this(
        id = "msg_${System.currentTimeMillis()}",
        senderType = senderType,
        sender = if (senderType == SenderType.USER) "user" else "ai",
        content = content,
        timestamp = timestamp,
        contextTag = null,
        trustBadge = null
    )

    /**
     * Secondary constructor for compatibility with existing string sender definitions.
     */
    constructor(
        id: String,
        sender: String,
        content: String,
        timestamp: String,
        contextTag: String? = null,
        trustBadge: String? = null
    ) : this(
        id = id,
        senderType = if (sender.equals("user", ignoreCase = true)) SenderType.USER else SenderType.AI,
        sender = sender,
        content = content,
        timestamp = timestamp,
        contextTag = contextTag,
        trustBadge = trustBadge
    )
}
