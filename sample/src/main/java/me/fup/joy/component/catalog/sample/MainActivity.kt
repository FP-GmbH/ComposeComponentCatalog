package me.fup.joy.component.catalog.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import me.fup.joy.component.catalog.ui.ComponentCatalogScreen
import me.fup.joy.components.ComponentCatalogMapMain

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComponentCatalogScreen(
                itemMap = ComponentCatalogMapMain.components,
            )
        }
    }
}