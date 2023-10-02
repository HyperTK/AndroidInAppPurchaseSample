package com.example.androidinapppurchasesample.ui.view

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidinapppurchasesample.R
import com.example.androidinapppurchasesample.SampleData
import com.example.androidinapppurchasesample.ui.theme.AndroidInAppPurchaseSampleTheme


data class Message(val author: String, val body: String)

@Composable
fun ConversationScreen(messages: List<Message>) {
    Conversation(messages = messages)
}

@Composable
fun MessageCard(msg: Message) {
    // パディング追加
    Row(Modifier.padding(all = 8.dp)) {
        Image(painter = painterResource(id = R.drawable.profile_picture),
            contentDescription = "Content profile picture",
            modifier = Modifier
                // 画像サイズ
                .size(40.dp)
                // 画像を丸で切り抜き
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape))

        // 画像と列の間に水平スペースを入れる
        Spacer(modifier = Modifier.width(8.dp))

        var isExpanded by remember {
            mutableStateOf(false)
        }

        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(text = msg.author,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium)
            // 間に垂直スペースを入れる
            Spacer(modifier = Modifier.height(4.dp))

            Surface(shape = MaterialTheme.shapes.medium, shadowElevation = 1.dp) {
                Text(
                    text = msg.body,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    AndroidInAppPurchaseSampleTheme {
        Conversation(messages = SampleData.conversationSample)
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    AndroidInAppPurchaseSampleTheme {
        Surface {
            MessageCard(
                msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!")
            )
        }
    }

}