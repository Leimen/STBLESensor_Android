/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.welcome.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.st.ui.composables.BlueMsButton
import com.st.ui.theme.Grey6
import com.st.ui.theme.LocalDimensions
import com.st.ui.theme.PreviewBlueMSTheme
import com.st.ui.utils.asString
import com.st.welcome.R
import com.st.welcome.StWelcomeConfig
import com.st.welcome.model.WelcomePage

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    welcomePages: List<WelcomePage> = emptyList(),
    onSkip: () -> Unit = { /** NOOP **/ }
) {
    val pagerState = rememberPagerState { welcomePages.size }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = LocalDimensions.current.paddingSmall)
    ) {
        HorizontalPager(
            modifier = Modifier.weight(weight = 0.1f),
            verticalAlignment = Alignment.Top,
            state = pagerState
        ) { pageIndex ->
            val imageId = welcomePages[pageIndex].drawableRes
            val title = welcomePages[pageIndex].title
            val description = welcomePages[pageIndex].description

            WelcomePageContent(
                modifier = Modifier.fillMaxSize(),
                imageId = imageId,
                title = title,
                description = description
            )
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(all = LocalDimensions.current.paddingNormal),
            indicatorCount = welcomePages.size,
            pagerState = pagerState
        )

        BlueMsButton(
            modifier = Modifier
                .align(alignment = Alignment.End),
            text =
            if (pagerState.currentPage == pagerState.pageCount - 1)
                stringResource(id = R.string.st_welcome_closeButtonLabel)
            else
                stringResource(id = R.string.st_welcome_skipButtonLabel),
            onClick = onSkip
        )
    }
}

@Composable
fun WelcomePageContent(
    modifier: Modifier,
    imageId: Int,
    title: String,
    description: String
) {
    Column(
        modifier = modifier.padding(all = LocalDimensions.current.paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(height = LocalDimensions.current.spacerSmall))

        Image(
            //modifier = Modifier.fillMaxWidth(),
            modifier = Modifier.fillMaxHeight(0.75f),
            contentScale = ContentScale.Inside,
            painter = painterResource(id = imageId),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(height = LocalDimensions.current.spacerSmall))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            maxLines = StWelcomeConfig.maxLinesTitle,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary,
            text = title
        )

        Spacer(modifier = Modifier.height(height = LocalDimensions.current.spacerSmall))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            maxLines = StWelcomeConfig.maxLinesDescription,
            overflow = TextOverflow.Ellipsis,
            color = Grey6,
            text = description
        )
    }
}

/** ----------------------- PREVIEW --------------------------------------- **/

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    PreviewBlueMSTheme {
        WelcomeScreen(
            modifier = Modifier,
            welcomePages = listOf(
                WelcomePage(
                    title = LoremIpsum(words = 3).asString(),
                    description = LoremIpsum(words = 20).asString(),
                    drawableRes = R.drawable.placeholder
                )
            )
        )
    }
}
