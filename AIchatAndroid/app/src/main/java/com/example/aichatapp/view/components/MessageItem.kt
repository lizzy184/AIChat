
package com.example.aichatapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.aichatapp.model.Message
import com.mikepenz.markdown.m3.Markdown

// =====================================================
// Markdown 格式优化
// 将 Markdown 标题转换为粗体，避免标题过大
// =====================================================

private fun formatMarkdown(text: String): String {
    return text.replace(
        Regex(
            "^#{1,6}\\s+(.*)$",
            RegexOption.MULTILINE
        ),
        "**$1**"
    )
}

// =====================================================
// 消息气泡基础样式
// =====================================================

private fun bubbleModifier(
    color: Color
): Modifier {
    return Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(color)
        .padding(
            horizontal = 16.dp,
            vertical = 12.dp
        )
}

// =====================================================
// 消息组件
// =====================================================

@Composable
fun MessageItem(
    message: Message
) {
    val colorScheme = MaterialTheme.colorScheme

    // 用户消息：浅色模式使用淡紫色，深色模式使用主题强调色
    val userBubbleColor = colorScheme.primary
    val userTextColor = colorScheme.onPrimary

    // AI 消息：使用主题卡片颜色和对应的正文颜色
    val aiBubbleColor = colorScheme.surface
    val aiTextColor = colorScheme.onSurface

    // 时间使用次要文字颜色
    val timeTextColor = colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = if (message.isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Column(
            modifier = Modifier.widthIn(max = 320.dp)
        ) {

            // =====================================================
            // 用户消息
            // =====================================================

            if (message.isUser) {
                Text(
                    text = message.text,
                    modifier = bubbleModifier(userBubbleColor),
                    color = userTextColor,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }

            // =====================================================
            // AI 消息
            // =====================================================

            else {
                if (message.isStreaming) {

                    // 流式生成时使用普通 Text，避免 Markdown 标题
                    // 在生成过程中导致布局跳动
                    Text(
                        text = message.text,
                        modifier = bubbleModifier(aiBubbleColor),
                        color = aiTextColor,
                        fontSize = 16.sp,
                        lineHeight = 26.sp
                    )

                } else {

                    // 生成结束后使用 Markdown
                    // 将气泡背景、正文颜色都设置为当前主题颜色
                    Column(
                        modifier = bubbleModifier(aiBubbleColor)
                    ) {
                        Markdown(
                            content = formatMarkdown(message.text),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // =====================================================
            // 消息时间
            // =====================================================

            Text(
                text = message.time,
                modifier = Modifier.padding(top = 4.dp),
                color = timeTextColor,
                fontSize = 12.sp
            )
        }
    }
}
