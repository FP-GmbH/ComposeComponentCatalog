package me.fup.joy.component.catalog.sample

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import me.fup.joy.component.catalog.core.ComponentCatalogItem

@Composable
fun ExtendedButtonComponent(
    text: String,
    onClick: () -> Unit,
) {
    Button(onClick = onClick) {
        Text(
            text,
            color = Color.White,
            modifier = Modifier.background(Color.Red)
        )
    }
}

data class ExtendedButtonPreviewParameter(
    val message: String,
)

class ExtendedButtonPreviewParameterProvider : PreviewParameterProvider<ExtendedButtonPreviewParameter> {
    override val values = sequenceOf(
        ExtendedButtonPreviewParameter(
            message = "Hello One",
        ),
        ExtendedButtonPreviewParameter(
            message = "Hello Two",
        ),
    )
}

@Preview
@Composable
@ComponentCatalogItem
fun ExtendedButtonComponentPreview(
    @PreviewParameter(ExtendedButtonPreviewParameterProvider::class) param: ExtendedButtonPreviewParameter
) {

    ExtendedButtonComponent(text = param.message) {
        Log.i("ButtonComponent", "onClick")
    }
}