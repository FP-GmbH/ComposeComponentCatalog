@file:OptIn(ExperimentalMaterial3Api::class)

package me.fup.joy.component.catalog.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val spaceOneUnit = 8.dp
val spaceTwoUnits = spaceOneUnit * 2

object ComponentCatalogScreen {

    @Composable
    operator fun invoke(
        itemMap: Map<String, @Composable () -> Unit>,
        title: String = stringResource(id = R.string.catalog_top_bar_title),
        colors: Colors = defaultColors(),
        itemBuilder: @Composable (title: String, component: @Composable () -> Unit, modifier: Modifier) -> Unit = { title, component, modifier ->
            ItemContainer(
                colors = colors,
                title = title,
                component = component,
                modifier = modifier
            )
        }
    ) {
        val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        var input: String by remember { mutableStateOf("") }
        val items by remember {
            derivedStateOf { itemMap.filter { it.key.contains(input, ignoreCase = true) } }
        }

        Scaffold(
            containerColor = colors.screenBackgroundColor,
            topBar = {
                Column(
                    modifier = Modifier.background(colors.topBarBackgroundColor)
                ) {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = colors.topBarBackgroundColor),
                        title = {
                            Text(
                                title,
                                color = colors.titleTextColor
                            )
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.content_description_back_button),
                                modifier = Modifier.clickable { onBackPressedDispatcher?.onBackPressed() },
                            )
                        },
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = input,
                            onValueChange = { input = it },
                            placeholder = { Text(stringResource(id = R.string.placeholder_search)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = spaceTwoUnits, end = spaceTwoUnits, bottom = spaceTwoUnits)
                        )
                    }
                }
            },
        ) { padding ->
            ComponentCatalogContent(
                items = items,
                itemContainer = itemBuilder,
                modifier = Modifier.padding(padding)
            )
        }
    }

    @Composable
    fun ComponentCatalogContent(
        items: Map<String, @Composable () -> Unit>,
        itemContainer: @Composable (title: String, component: @Composable () -> Unit, modifier: Modifier) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val localDensity = LocalDensity.current
        var maxItemHeight by remember { mutableStateOf(0.dp) }

        Column(
            modifier = modifier
        ) {
            LazyColumn(
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    maxItemHeight = with(localDensity) { coordinates.size.height.toDp() }
                }
            ) {
                itemsIndexed(
                    items = items.entries.toList()
                ) { index, item ->
                    itemContainer(
                        item.key,
                        item.value,
                        Modifier
                            .padding(spaceTwoUnits)
                            .heightIn(max = maxItemHeight)
                    )
                }
            }
        }
    }

    @Composable
    fun ItemContainer(
        colors: Colors,
        title: String,
        component: @Composable () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        Surface(
            shape = RoundedCornerShape(spaceOneUnit),
            color = colors.cardBackgroundColor,
            modifier = modifier
        ) {
            Column {
                Text(
                    text = title,
                    color = colors.cardTitleTextColor,
                    style = TextStyle(fontSize = 22.sp),
                    modifier = Modifier
                        .background(colors.cardHeadlineColor)
                        .fillMaxWidth()
                        .padding(start = spaceTwoUnits, end = spaceTwoUnits, top = spaceTwoUnits, bottom = spaceOneUnit)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spaceTwoUnits),
                    contentAlignment = Alignment.Center
                ) {
                    component()
                }
            }
        }
    }

    class Colors(
        val titleTextColor: Color,
        val topBarBackgroundColor: Color,
        val screenBackgroundColor: Color,
        val cardTitleTextColor: Color,
        val cardHeadlineColor: Color,
        val cardBackgroundColor: Color,
    )

    @Composable
    fun defaultColors(): Colors = Colors(
        titleTextColor = MaterialTheme.colorScheme.onPrimary,
        topBarBackgroundColor = MaterialTheme.colorScheme.primary,
        screenBackgroundColor = MaterialTheme.colorScheme.surface,
        cardTitleTextColor = MaterialTheme.colorScheme.onSecondary,
        cardHeadlineColor = MaterialTheme.colorScheme.secondary,
        cardBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    )
}