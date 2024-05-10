@file:OptIn(ExperimentalMaterial3Api::class)

package com.rfdotech.core.presentation.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.core.presentation.designsystem.AnalyticsIcon
import com.rfdotech.core.presentation.designsystem.ArrowLeftIcon
import com.rfdotech.core.presentation.designsystem.LogoIcon
import com.rfdotech.core.presentation.designsystem.SignOutIcon
import com.rfdotech.core.presentation.designsystem.R
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space32
import com.rfdotech.core.presentation.designsystem.Space8
import com.rfdotech.core.presentation.designsystem.components.util.DropDownItem
import com.rfdotech.core.presentation.designsystem.primaryFontFamily

@Composable
fun PrimaryToolbar(
    title: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    menuItems: List<DropDownItem> = emptyList(),
    onMenuItemClick: (index: Int) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    startContent: (@Composable () -> Unit)? = null
) {

    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                startContent?.invoke()
                Spacer(modifier = Modifier.width(Space8))
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = primaryFontFamily
                )
            }
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ArrowLeftIcon,
                        contentDescription = stringResource(id = R.string.go_back),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            if (menuItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                ) {
                    DropdownMenu(
                        expanded = isDropDownOpen,
                        onDismissRequest = {
                            isDropDownOpen = false
                        }
                    ) {
                        menuItems.forEachIndexed { index, item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable {
                                        onMenuItemClick(index)
                                    }
                                    .fillMaxWidth()
                                    .padding(Space16)
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                                Spacer(modifier = Modifier.width(Space8))
                                Text(
                                    text = item.title
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            isDropDownOpen = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(id = R.string.open_drop_down)
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun PrimaryToolbarPreview() {
    RunItTheme {
        PrimaryToolbar(
            title = "RunIt",
            modifier = Modifier.fillMaxWidth(),
            startContent = {
                Icon(
                    imageVector = LogoIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Space32)
                )
            },
            menuItems = listOf(
                DropDownItem(icon = AnalyticsIcon, title = stringResource(id = R.string.analytics)),
                DropDownItem(icon = SignOutIcon, title = stringResource(id = R.string.sign_out))
            )
        )
    }
}