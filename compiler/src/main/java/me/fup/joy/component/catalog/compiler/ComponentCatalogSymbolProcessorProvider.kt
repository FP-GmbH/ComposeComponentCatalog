package me.fup.joy.component.catalog.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

const val MODULE_NAME_KEY = "module"

class ComponentCatalogSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val logger = environment.logger

        val moduleName = runCatching { environment.options.getValue(MODULE_NAME_KEY) }.getOrNull() ?: "main"
        val generator = Generator(moduleName, logger, environment.codeGenerator)

        return ComponentCatalogSymbolProcessor(logger, generator)
    }
}