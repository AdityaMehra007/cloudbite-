package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AiAssistantScreen(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val messages by viewModel.aiMessages.collectAsState()
  val listState = rememberLazyListState()

  val suggestedPrompts = listOf(
    "Analyze our recipe contribution margins",
    "Which ingredients are near low-stock threshold?",
    "Forecast Bengaluru weekend dinner rush demand",
    "Evaluate theoretical ingredient wastage variance"
  )

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianCharcoal)
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(CharcoalElevated)
            .border(1.dp, MutedChampagne, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MutedChampagne,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "BLACK LABEL INTELLIGENCE",
            style = MaterialTheme.typography.labelSmall,
            color = MutedChampagne,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "AI Culinary & Financial Partner",
            style = MaterialTheme.typography.titleMedium,
            color = PorcelainIvory
          )
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CharcoalSurface)
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        // Quick suggestion chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          items(suggestedPrompts) { prompt ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, AntiqueBrass.copy(alpha = 0.5f)),
              modifier = Modifier.clickable { viewModel.sendAiQuery(prompt) }
            ) {
              Text(
                text = prompt,
                style = MaterialTheme.typography.labelSmall,
                color = MutedChampagne,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = uiState.aiInputQuery,
            onValueChange = { viewModel.setAiQuery(it) },
            placeholder = { Text("Ask about recipes, margins, inventory, forecasts...", color = MutedSecondaryText, fontSize = 12.sp) },
            modifier = Modifier
              .weight(1f)
              .testTag("ai_query_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = PorcelainIvory,
              unfocusedTextColor = PorcelainIvory,
              focusedBorderColor = MutedChampagne,
              unfocusedBorderColor = WarmBorderDark
            ),
            singleLine = true
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = { viewModel.sendAiQuery() },
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(RefinedTerracotta)
              .testTag("send_ai_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = PaperWhite
            )
          }
        }
      }
    },
    containerColor = ObsidianCharcoal,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      items(messages) { (sender, text) ->
        val isUser = sender == "Operator"
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
          horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
          Surface(
            shape = RoundedCornerShape(
              topStart = 12.dp,
              topEnd = 12.dp,
              bottomStart = if (isUser) 12.dp else 2.dp,
              bottomEnd = if (isUser) 2.dp else 12.dp
            ),
            color = if (isUser) RefinedTerracotta else CharcoalElevated,
            border = if (isUser) null else BorderStroke(1.dp, WarmBorderDark),
            modifier = Modifier.widthIn(max = 320.dp)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = sender.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isUser) WarmCream else MutedChampagne,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = PorcelainIvory,
                lineHeight = 20.sp
              )
            }
          }
        }
      }

      if (uiState.isAiThinking) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CharcoalElevated,
              border = BorderStroke(1.dp, WarmBorderDark)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = MutedChampagne,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Analyzing kitchen telemetry & financial model...",
                  style = MaterialTheme.typography.bodySmall,
                  color = MutedLightText
                )
              }
            }
          }
        }
      }
    }
  }
}
