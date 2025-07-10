ComposeComponentCatalog
========

[![](https://jitpack.io/v/FP-GmbH/ComposeComponentCatalog.svg)](https://jitpack.io/#FP-GmbH/ComposeComponentCatalog)

ComposeComponentCatalog is a lightweight library that helps you create and maintain a visual catalog of your Jetpack Compose UI components.
It uses Kotlin Symbol Processing (KSP) to generate a catalog of your annotated components that you can easily display within your app.

Getting started
---------------
Add Jitpack maven repository to your settings.gradle with

```
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url = uri("https://jitpack.io") }
	}
}
```
Add dependencies to build.gradle of your module

```
dependencies {
    implementation("com.github.FP-GmbH.ComposeComponentCatalog:core:{latestVersion}")
    ksp("com.github.FP-GmbH.ComposeComponentCatalog:compiler:{latestVersion}")
}
```

How to use?
-----------

Annotate your composable function with `@ComponentCatalogItem`.

The annotation is intended for use with composable preview functions. The only technical limitation is that the annotated function is public and can be called without passing arguments.
Therefore, it can also be used for non-preview functions.

```
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

    ButtonComponent(text = "Hello World") {
        Log.i("ButtonComponent", "onClick")
    }
}
```

Build your module to start code generation. After that you can use the generated `ComponentCatalogMapMain` to display your component catalog in any way you want.

ComposeComponentCatalog-UI
-------------------

If you want a predefined catalog add the ui dependency to your module and add `ComponentCatalogScreen` to your ui. There are optional parameters `colors` and `itemBuilder` to do slight adjustments.

```
dependencies {
    implementation("com.github.FP-GmbH.ComposeComponentCatalog:ui:{latestVersion}")
}
```

```
ComponentCatalogScreen(
    itemMap = ComponentCatalogMapMain.components,
)
```

<p align="center">
    <img src="https://github.com/FP-GmbH/ComposeComponentCatalog/blob/main/preview/ComponentCatalogUi.png?raw=true" alt="catalog preview" width="200"/>
</p>

Change displayed component name
-------------------------------
By default the method name of your annotated method is used as key for `ComponentCatalogMapMain` and as title of the catalog item if you use default ui.
You can change that by passing a optional `name` argument to the annotation.

```
@Preview
@Composable
@ComponentCatalogItem(name = "CustomButton")
fun ButtonComponentPreview() {

    ButtonComponent(text = "Hello World") {
        Log.i("ButtonComponent", "onClick")
    }
}
```

Multi-Module usage
------------------
If your project has a multi module setup your probably want to use the code generation in several modules. The code generation creates a `me.fup.joy.components.ComponentCatalogMapMain` for each module.
To prevent conflicts by using multiple `ComponentCatalogMapMain` from multiple modules you have to set a module name as ksp argument in the build.gradle of your modules.
The provided module argument will be used as suffix for the generated class like `ComponentCatalogMap{ModuleName}`.

```
android {
    ...

    defaultConfig {
        ...

        ksp {
            arg("module", project.name)
        }
    }
}
```

Usage with Preview-Parameter
----------------------------
If a compose preview function which uses PreviewParameter is annotated, the resulting `ComponentCatalogMapMain` contains multiple entities which keys are suffixed by the corresponding
index of the parameter in the list provided by the PreviewParameterProvider.

```
data class ButtonPreviewParameter(
    val message: String,
)

class ButtonPreviewParameterProvider : PreviewParameterProvider<ButtonPreviewParameter> {
    override val values = sequenceOf(
        ButtonPreviewParameter(
            message = "Hello One",
        ),
        ButtonPreviewParameter(
            message = "Hello Two",
        ),
    )
}

@Preview
@Composable
@ComponentCatalogItem
fun ButtonComponentPreview(
    @PreviewParameter(ButtonPreviewParameterProvider::class) param: ButtonPreviewParameter
) {

    ButtonComponent(text = param.message) {
        Log.i("ButtonComponent", "onClick")
    }
}
```

This will generate entities for `ComponentCatalogMapMain` like

```
    ButtonPreviewParameterProvider().values.forEachIndexed { index, value -> privateComponents["ButtonComponentPreview$index"] = { ButtonComponentPreview(value) }
```
