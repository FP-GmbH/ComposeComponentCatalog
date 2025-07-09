package me.fup.joy.component.catalog.sample

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import me.fup.joy.component.catalog.core.ComponentCatalogItem

@Composable
fun ButtonComponent(
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

@Preview
@Composable
@ComponentCatalogItem
fun ButtonComponentPreview() {

    ButtonComponent(text = "Hello Android") {
        Log.i("ButtonComponent", "onClick")
    }
}