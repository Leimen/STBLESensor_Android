/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */
package com.st.hight_speed_data_log

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.st.blue_sdk.board_catalog.models.DtmiContent
import com.st.high_speed_data_log.R
import com.st.hight_speed_data_log.composable.HsdlSensors
import com.st.hight_speed_data_log.composable.HsdlTags
import com.st.hight_speed_data_log.composable.StopLoggingDialog
import com.st.hight_speed_data_log.composable.VespucciHsdlTags
import com.st.pnpl.composable.PnPLInfoWarningSpontaneousMessage
import com.st.ui.composables.BlueMsButton
import com.st.ui.composables.CommandRequest
import com.st.ui.composables.ComposableLifecycle
import com.st.ui.theme.ErrorText
import com.st.ui.theme.Grey0
import com.st.ui.theme.Grey6
import com.st.ui.theme.LocalDimensions
import com.st.ui.theme.PrimaryBlue2
import com.st.ui.theme.SecondaryBlue
import com.st.ui.theme.Shapes
import kotlinx.serialization.json.JsonObject
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighSpeedDataLog(
    modifier: Modifier,
    viewModel: HighSpeedDataLogViewModel,
    nodeId: String
) {
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> viewModel.startDemo(nodeId = nodeId)
            Lifecycle.Event.ON_STOP -> viewModel.stopDemo(nodeId = nodeId)
            else -> Unit
        }
    }
    val isLogging by viewModel.isLogging.collectAsStateWithLifecycle()
    val sensors by viewModel.sensors.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val status by viewModel.componentStatusUpdates.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isSDCardInserted by viewModel.isSDCardInserted.collectAsStateWithLifecycle()
    val acquisitionName by viewModel.acquisitionName.collectAsStateWithLifecycle()
    val vespucciTags by viewModel.vespucciTags.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val isConnectionLost by viewModel.isConnectionLost.collectAsStateWithLifecycle()

    HighSpeedDataLog(
        modifier = modifier,
        sensors = sensors,
        tags = tags,
        status = status,
        isSDCardInserted = isSDCardInserted,
        isLogging = isLogging,
        isLoading = isLoading,
        vespucciTags = vespucciTags,
        acquisitionName = acquisitionName,
        onTagChangeState = { tag, newState ->
            viewModel.onTagChangeState(nodeId, tag, newState)
        },
        onValueChange = { name, value ->
            if (isLoading.not()) {
                viewModel.sendChange(
                    nodeId = nodeId,
                    name = name,
                    value = value
                )
            }
        },
        onSendCommand = { name, value ->
            if (isLoading.not()) {
                viewModel.sendCommand(
                    nodeId = nodeId,
                    name = name,
                    value = value
                )
            }
        },
        onStartStopLog = {
            if (it) {
                if (isLogging.not() && isLoading.not()) {
                    viewModel.startLog(nodeId)
                }
            } else {
                viewModel.stopLog(nodeId)
            }
        },
        onRefresh = {
            if (isLogging.not() && isLoading.not()) {
                viewModel.refresh(nodeId)
            }
        }
    )
 
    statusMessage?.let {
        PnPLInfoWarningSpontaneousMessage(messageType = statusMessage!!, onDismissRequest = { viewModel.cleanStatusMessage() })
    }

    if(isConnectionLost) {
        BasicAlertDialog(onDismissRequest = { viewModel.resetConnectionLost() })
        {
            Surface(
                modifier = Modifier
                    //.wrapContentWidth()
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = Shapes.medium
            ) {
                Column(modifier = Modifier.padding(all = LocalDimensions.current.paddingMedium)) {
                    Text(
                        text = "Warning:",
                        modifier = Modifier.padding(bottom = LocalDimensions.current.paddingNormal),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        letterSpacing = 0.15.sp,
                        color = ErrorText
                    )
                    Text(
                        text = "Lost Connection with the Node",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        letterSpacing = 0.25.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(Modifier.fillMaxWidth()
                        .padding(start = LocalDimensions.current.paddingSmall, end = LocalDimensions.current.paddingSmall, top= LocalDimensions.current.paddingNormal),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween) {

                        BlueMsButton(
                            modifier = Modifier.padding(end = LocalDimensions.current.paddingSmall),
                            text = "Close",
                            onClick = {
                                viewModel.disconnect()
                            }
                        )

                        BlueMsButton(
                            modifier = Modifier.padding(end = LocalDimensions.current.paddingSmall),
                            text = "Cancel",
                            onClick = {
                                viewModel.resetConnectionLost()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HighSpeedDataLog(
    modifier: Modifier,
    sensors: List<Pair<DtmiContent.DtmiComponentContent, DtmiContent.DtmiInterfaceContent>> = emptyList(),
    tags: List<Pair<DtmiContent.DtmiComponentContent, DtmiContent.DtmiInterfaceContent>> = emptyList(),
    status: List<JsonObject>,
    vespucciTags: Map<String, Boolean>,
    isLogging: Boolean,
    isSDCardInserted: Boolean = false,
    isLoading: Boolean = false,
    acquisitionName: String = "",
    onValueChange: (String, Pair<String, Any>) -> Unit,
    onSendCommand: (String, CommandRequest?) -> Unit,
    onTagChangeState: (String, Boolean) -> Unit = { _, _ -> /**NOOP**/ },
    onStartStopLog: (Boolean) -> Unit = { /**NOOP **/ },
    onRefresh: () -> Unit = { /**NOOP **/ },
    navController: NavHostController = rememberNavController()
) {
    val sensorsTitle = stringResource(id = R.string.st_hsdl_sensors)
    val tagsTitle = stringResource(id = R.string.st_hsdl_tags)
    var currentTitle by remember { mutableStateOf(sensorsTitle) }
    var openStopDialog by remember { mutableStateOf(value = false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    val haptic = LocalHapticFeedback.current

    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = {
            HsdlConfig.hsdlTabBar?.invoke(currentTitle, isLoading)
        },
//        floatingActionButtonPosition = FabPosition.EndOverlay,
//        floatingActionButton = {
//            FloatingActionButton(
//                containerColor = SecondaryBlue,
//                onClick = {
//                    if (isSDCardInserted) {
//                        if (isLogging) {
//                            onStartStopLog(false)
//                            openStopDialog = HsdlConfig.showStopDialog
//                        } else {
//                            onStartStopLog(true)
//                        }
//                    } else {
//                        Toast.makeText(
//                            context,
//                            context.getString(R.string.st_hsdl_missingSdCard),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            ) {
//                Icon(
//                    tint = MaterialTheme.colorScheme.primary,
//                    imageVector = if (isLogging) Icons.Default.Stop else Icons.Default.PlayArrow,
//                    contentDescription = null
//                )
//            }
//        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Grey0
            ) {
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Grey0,
                        selectedTextColor = Grey0,
                        unselectedIconColor = Grey6,
                        unselectedTextColor = Grey6,
                        indicatorColor = PrimaryBlue2
                    ),
                    selected = 0 == selectedIndex,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectedIndex = 0
                        currentTitle = sensorsTitle
                        navController.navigate("Sensors") {
                            navController.graph.startDestinationRoute?.let { screenRoute ->
                                popUpTo(screenRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sensors),
                            contentDescription = stringResource(id = R.string.st_hsdl_sensors)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.st_hsdl_sensors)) },
                    enabled = !isLoading
                )

                FloatingActionButton(
                    containerColor = SecondaryBlue,
                    onClick = {
                        if (isSDCardInserted) {
                            if (isLogging) {
                                onStartStopLog(false)
                                openStopDialog = HsdlConfig.showStopDialog
                            } else {
                                onStartStopLog(true)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.st_hsdl_missingSdCard),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        imageVector = if (isLogging) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }

                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Grey0,
                        selectedTextColor = Grey0,
                        unselectedIconColor = Grey6,
                        unselectedTextColor = Grey6,
                        indicatorColor = PrimaryBlue2
                    ),
                    selected = 1 == selectedIndex,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectedIndex = 1
                        currentTitle = tagsTitle
                        navController.navigate("Tags") {
                            navController.graph.startDestinationRoute?.let { screenRoute ->
                                popUpTo(screenRoute) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_tags),
                            contentDescription = stringResource(id = R.string.st_hsdl_tags)
                        )
                    },
                    label = { Text(text = stringResource(id = R.string.st_hsdl_tags)) },
                    enabled = !isLoading
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.pullRefresh(state = pullRefreshState)) {
            NavHost(
                modifier = modifier.padding(paddingValues),
                navController = navController,
                startDestination = "Sensors"
            ) {
                composable(
                    route = "Sensors"
                ) {
                    if (isLogging.not()) {
                        HsdlSensors(
                            sensors = sensors,
                            status = status,
                            isLoading = isLoading,
                            onValueChange = onValueChange,
                            onSendCommand = onSendCommand
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(id = R.string.st_hsdl_logging))
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }

                    }
                }

                composable(
                    route = "Tags"
                ) {
                    if (HsdlConfig.tags.isEmpty()) {
                        HsdlTags(
                            tags = tags,
                            status = status,
                            isLoading = isLoading,
                            onValueChange = onValueChange,
                            onSendCommand = onSendCommand
                        )
                    } else {
                        VespucciHsdlTags(
                            acquisitionInfo = acquisitionName.formatDate(),
                            vespucciTags = vespucciTags,
                            isLoading = isLoading,
                            isLogging = isLogging,
                            onTagChangeState = onTagChangeState
                        )
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(alignment = Alignment.TopCenter),
                scale = true
            )
        }
    }

    if (openStopDialog) {
        StopLoggingDialog(
            onDismissRequest = { openStopDialog = false }
        )
    }
}


fun String.formatDate(): String {
    val inputSdf = SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.ROOT)
    val date = inputSdf.parse(this)
    val sdf = SimpleDateFormat("EEE MMM d yyyy HH:mm:ss", Locale.UK)
    return sdf.format(date)
}
