@file:OptIn(ExperimentalFoundationApi::class)

package com.rfdotech.core.presentation.designsystem.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.rfdotech.core.presentation.designsystem.CheckIcon
import com.rfdotech.core.presentation.designsystem.EmailIcon
import com.rfdotech.core.presentation.designsystem.EyeClosedIcon
import com.rfdotech.core.presentation.designsystem.EyeOpenedIcon
import com.rfdotech.core.presentation.designsystem.LockIcon
import com.rfdotech.core.presentation.designsystem.NoSpace
import com.rfdotech.core.presentation.designsystem.R
import com.rfdotech.core.presentation.designsystem.RunItTheme
import com.rfdotech.core.presentation.designsystem.Space1
import com.rfdotech.core.presentation.designsystem.Space12
import com.rfdotech.core.presentation.designsystem.Space16
import com.rfdotech.core.presentation.designsystem.Space8

@Composable
fun PrimaryTextField(
    state: TextFieldState,
    hint: String,
    modifier: Modifier = Modifier,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    title: String? = null,
    error: String? = null,
    additionalInfo: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (title != null) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            } else if (additionalInfo != null) {
                Text(
                    text = additionalInfo,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(Space8))

        val textFieldShape = RoundedCornerShape(Space16)

        val textFieldBackground = if (isFocused) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        }

        val textFieldBorderColor = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }

        BasicTextField2(
            state = state,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .clip(textFieldShape)
                .background(textFieldBackground)
                .border(
                    width = Space1,
                    color = textFieldBorderColor,
                    shape = textFieldShape
                )
                .padding(Space12)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            decorator = { innerBox ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (startIcon != null) {
                        Icon(
                            imageVector = startIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(Space16))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if (state.text.isBlank() && !isFocused) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }
                    if (endIcon != null) {
                        Spacer(modifier = Modifier.width(Space16))
                        Icon(
                            imageVector = endIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = Space8)
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun PrimaryTextFieldPreview() {
    RunItTheme {
        PrimaryTextField(
            state = rememberTextFieldState(),
            hint = "example@test.com",
            title = "Email",
            additionalInfo = "Must be a valid email",
            startIcon = EmailIcon,
            endIcon = CheckIcon,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasswordTextField(
    state: TextFieldState,
    hint: String,
    title: String,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    isPasswordVisible: Boolean = false
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(Space8))

        val textFieldShape = RoundedCornerShape(Space16)

        val textFieldBackground = if (isFocused) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        }

        val textFieldBorderColor = if (isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }

        val textObfuscationMode = if (isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.Hidden
        }

        BasicSecureTextField(
            state = state,
            textObfuscationMode = textObfuscationMode,
            keyboardType = KeyboardType.Password,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .clip(textFieldShape)
                .background(textFieldBackground)
                .border(
                    width = Space1,
                    color = textFieldBorderColor,
                    shape = textFieldShape
                )
                .padding(horizontal = Space12)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            decorator = { innerBox ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = LockIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Space16))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if (state.text.isBlank() && !isFocused) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }

                    val passwordVisibilityIcon = if (isPasswordVisible) {
                        EyeOpenedIcon
                    } else {
                        EyeClosedIcon
                    }

                    val passwordToggleContentDesc = if (isPasswordVisible) {
                        stringResource(id = R.string.acc_hide_password)
                    } else {
                        stringResource(id = R.string.acc_show_password)
                    }

                    Spacer(modifier = Modifier.width(Space16))
                    IconButton(
                        onClick = onTogglePasswordVisibility,
                        modifier = Modifier.padding(NoSpace)
                    ) {
                        Icon(
                            imageVector = passwordVisibilityIcon,
                            contentDescription = passwordToggleContentDesc,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
private fun PasswordTextFieldPreview() {
    RunItTheme {
        PasswordTextField(
            state = rememberTextFieldState(),
            hint = "",
            title = "Password",
            onTogglePasswordVisibility = {},
            isPasswordVisible = false
        )
    }
}