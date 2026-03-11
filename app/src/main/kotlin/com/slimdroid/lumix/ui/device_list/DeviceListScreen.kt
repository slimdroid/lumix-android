package com.slimdroid.lumix.ui.device_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.slimdroid.lumix.core.model.LumixDevice
import com.slimdroid.lumix.ui.shape.RoundedPolygonShape
import com.slimdroid.lumix.ui.theme.AppTheme
import kotlin.math.PI
import kotlin.math.sin

private val OrangeColor = Color(255, 197, 2)
private val FAB_SIZE_LARGE = 96.dp
private val CORNER_RADIUS = 32.dp
private val ITEM_PADDING = 16.dp
private val ITEM_BORDER = 8.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeviceListScreen(
    state: DeviceListUiState,
    onSearchClick: () -> Unit,
    onDeviceClick: (device: LumixDevice) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults
                    .topAppBarColors()
                    .copy(containerColor = Color.Transparent),
                title = {
                    Image(
                        imageVector = FieldbeeLogo,
                        contentDescription = null,
                        modifier = Modifier.height(32.dp),
                        colorFilter = ColorFilter.tint(color = LocalContentColor.current),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp),
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .padding(padding)
        ) {
            when (state) {
                is DeviceListUiState.Empty -> Unit
                is DeviceListUiState.Content -> {
                    DeviceList(state.deviceList) { device ->
                        onDeviceClick.invoke(device)
                    }
                    if (state.isProgress) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.statusBars)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceList(
    deviceList: List<LumixDevice>, onItemClick: (device: LumixDevice) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val itemSize by remember {
            mutableStateOf(min(maxHeight, maxWidth) / 7 * 4 - ITEM_PADDING * 1.732f)
        }
        val overlap by remember { mutableStateOf(itemOverlap(itemSize) + ITEM_PADDING / 2) }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(overlap),
            contentPadding = PaddingValues(
                bottom = FAB_SIZE_LARGE
                        + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                        + 16.dp,
                top = TopAppBarDefaults.MediumAppBarCollapsedHeight
                        + WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            )
        ) {
            itemsIndexed(items = deviceList, key = { _, item -> item.toString() }) { index, item ->
                HoneycombItem(item, index, itemSize, onItemClick)
            }
        }
    }
}

@Composable
internal fun HoneycombItem(
    device: LumixDevice,
    index: Int,
    hexagonSize: Dp,
    onItemClick: (device: LumixDevice) -> Unit
) {
    val paddingValue by remember { mutableStateOf(ITEM_PADDING * 1.866f + hexagonSize * .75f) }
    val itemShape by remember {
        mutableStateOf(
            RoundedPolygonShape(sides = 6, fillMaxSize = false, cornerRadius = CORNER_RADIUS)
        )
    }

    Card(
        onClick = { onItemClick(device) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .padding(
                start = if (index % 2 == 1) paddingValue else ITEM_PADDING,
                end = if (index % 2 == 0) paddingValue else ITEM_PADDING,
            )
            .size(hexagonSize)
            .clip(itemShape)
            .border(ITEM_BORDER, OrangeColor, itemShape)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = device.macAddress, color = MaterialTheme.colorScheme.tertiary
            )
//            Image(
//                imageVector = getIcon(device.type),
//                contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(hexagonSize / 2)
//            )
            Text(
                text = device.ipAddress, fontSize = 12.sp, color = OrangeColor
            )
        }
    }
}

private fun itemOverlap(size: Dp): Dp {
    val angle = 2.0 * PI / 6
    val radius = size / 2f
    return -radius * (2f - sin(angle).toFloat())
}

fun Double.toRadian(): Double = (this / 180 * PI)
fun Double.toDegree(): Double = (this * 180 / PI)

@Preview
@Composable
private fun DeviceListScreenPreview(
    @PreviewParameter(DeviceListPreviewProvider::class)
    uiState: DeviceListUiState
) {
    AppTheme {
        Surface {
            DeviceListScreen(
                state = uiState,
                onSearchClick = {},
                onDeviceClick = {}
            )
        }
    }

}

@PreviewLightDark
@Composable
private fun HoneycombItemPreview(
    @PreviewParameter(DeviceListPreviewProvider::class)
    uiState: DeviceListUiState.Content
) {
    AppTheme {
        Surface {
            HoneycombItem(
                device = uiState.deviceList.first(),
                index = 0,
                hexagonSize = 250.dp,
                onItemClick = {}
            )
        }
    }
}
