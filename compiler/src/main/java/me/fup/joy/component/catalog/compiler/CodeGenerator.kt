package me.fup.joy.component.catalog.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MAP
import com.squareup.kotlinpoet.MUTABLE_MAP
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import me.fup.joy.component.catalog.core.ComponentCatalogItem

const val GENERATED_OBJECT_NAME = "ComponentCatalogMap"
const val PACKAGE = "me.fup.joy.components"

class Generator(
    private val moduleName: String,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
) {

    private val functions = mutableListOf<KSFunctionDeclaration>()
    private val initializerCodeBlocks = mutableListOf<CodeBlock>()
    private val imports = mutableListOf<ImportData>()

    private val previewParameterAnnotation = createAnnotation("androidx.compose.ui.tooling.preview", "PreviewParameter")

    fun add(function: KSFunctionDeclaration) {
        if (functions.find { it.simpleName.asString() == function.simpleName.asString() } != null) return

        functions.add(function)
        val initializerData = createInitializer(function)

        initializerCodeBlocks.add(initializerData.codeBlock)
        imports.add(ImportData(function.packageName.asString(), function.simpleName.getShortName()))
        initializerData.import?.let { imports.add(ImportData(it.packageName, it.className)) }
    }

    fun write() {
        val className = "$GENERATED_OBJECT_NAME${moduleName.replaceFirstChar(Char::titlecase)}"
        val fileSpec = FileSpec.builder(PACKAGE, "$className.kt")
            .addType(
                TypeSpec.objectBuilder(className)
                    .addProperty(
                        createMapFieldBuilder(FIELD_NAME_MAP_INTERNAL, MUTABLE_MAP)
                            .addModifiers(KModifier.PRIVATE)
                            .initializer(
                                CodeBlock.of("mutableMapOf()")
                            ).build()
                    )
                    .addProperty(
                        createMapFieldBuilder(FIELD_NAME_MAP_PUBLIC, MAP)
                            .getter(
                                FunSpec.getterBuilder()
                                    .addStatement("return ${FIELD_NAME_MAP_INTERNAL}")
                                    .build()
                            ).build()
                    )
                    .addInitializerBlock(
                        CodeBlock.builder().apply {
                            initializerCodeBlocks.forEach { block -> add(block) }
                        }.build()
                    ).build()
            )

        imports.forEach { fileSpec.addImport(it.packageName, it.className) }

        val dependencies = Dependencies(true, *functions.mapNotNull { it.containingFile }.toTypedArray())
        fileSpec.build().writeTo(codeGenerator, dependencies)
    }

    private fun createInitializer(function: KSFunctionDeclaration): ComponentInitializer {
        val codeBlock = CodeBlock.builder()
        var importData: ImportData? = null

        val functionName = getFunctionName(function)
        if (function.parameters.isEmpty()) {
            codeBlock.add("$FIELD_NAME_MAP_INTERNAL[\"$functionName\"] = { ${function.simpleName.getShortName()}() }")
        } else {
            val parameter = findPreviewParameterAnnotation(function)
            parameter?.annotations?.find { annotation -> annotation.annotationType.toTypeName() == previewParameterAnnotation.typeName }?.let {
                val providerClass = it.arguments[0].value.toString()
                importData = ImportData(function.packageName.asString(), providerClass)

                val leftSide = "$FIELD_NAME_MAP_INTERNAL[\"$functionName\$index\"]"
                val rightSide = "{ ${function.simpleName.getShortName()}(value) }"
                codeBlock.add(
                    "${providerClass}().values.forEachIndexed { index, value -> " +
                            "$leftSide = $rightSide"
                            + "\n}"
                )
            }
        }

        codeBlock.add("\n")

        return ComponentInitializer(codeBlock.build(), importData)
    }

    @OptIn(KspExperimental::class)
    private fun getFunctionName(function: KSFunctionDeclaration): String {
        val nameArg = function.getAnnotationsByType(ComponentCatalogItem::class).firstOrNull()?.name

        return if (nameArg.isNullOrBlank()) {
            function.simpleName.getShortName()
        } else nameArg
    }

    private fun findPreviewParameterAnnotation(functionDeclaration: KSFunctionDeclaration): KSValueParameter? {
        val params = functionDeclaration.parameters.find { param ->
            param.annotations.find { annotation ->
                annotation.annotationType.toTypeName() == previewParameterAnnotation.typeName
            } != null
        }

        return params
    }

    private fun createMapFieldBuilder(name: String, typeName: ClassName): PropertySpec.Builder {
        return PropertySpec.builder(
            name, typeName.parameterizedBy(
                String::class.asClassName(),
                LambdaTypeName.get(returnType = Unit::class.asTypeName())
                    .copy(annotations = listOf(createAnnotation("androidx.compose.runtime", "Composable")))
            )
        )
    }

    private fun createAnnotation(packageName: String, className: String): AnnotationSpec {
        return AnnotationSpec.builder(ClassName(packageName, className)).build()
    }

    data class ComponentInitializer(
        val codeBlock: CodeBlock,
        val import: ImportData?,
    )

    data class ImportData(
        val packageName: String,
        val className: String,
    )

    companion object {

        const val FIELD_NAME_MAP_INTERNAL = "privateComponents"
        const val FIELD_NAME_MAP_PUBLIC = "components"
    }
}