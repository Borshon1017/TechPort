package com.example.techport.ui.about

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techport.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    var randomQuote by remember { mutableStateOf("Tap below to get a random tech quote!") }

    val localQuotes = listOf(
        "Code is like humor. When you have to explain it, it’s bad.",
        "Talk is cheap. Show me the code. – Linus Torvalds",
        "In theory, theory and practice are the same. In practice, they’re not.",
        "The best error message is the one that never shows up.",
        "Programming is 10% writing code and 90% understanding why it doesn’t work.",
        "Software comes from heaven when you have good hardware.",
        "Before software can be reusable, it first has to be usable.",
        "Simplicity is the soul of efficiency."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About TechPort", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            //  App Logo
            Image(
                painter = painterResource(id = R.drawable.splashtest),
                contentDescription = "TechPort Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            //  App Name
            Text(
                text = "TechPort",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            //  Version
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App Description
            Text(
                text = "TechPort is a collaborative mobile app built by a dedicated team to connect users with trusted tech repair experts. It integrates Firebase, Maps, and Jetpack Compose for a seamless and modern user experience — even offline.",
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            //  Tech Quote Spinner (with animation)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { randomQuote = localQuotes.random() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF101010))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎮 Tech Quote Spinner",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedContent(
                        targetState = randomQuote,
                        transitionSpec = {
                            (fadeIn() + scaleIn(initialScale = 0.9f))
                                .togetherWith(fadeOut())
                        },
                        label = "quoteTransition"
                    ) { quote ->
                        Text(
                            text = quote,
                            color = Color(0xFFEEEEEE),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            //  Team Credits
            Text(
                text = "Developed by the TechPort Team",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Naomi Nketsiah · Borshon Alfred Goles · David Chiemerie Ekweanua · Umair Atique",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✉ Contact
            Text(
                text = "📧 techportapp.team@gmail.com",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
