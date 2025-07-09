package me.fup.joy.component.catalog.core

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ComponentCatalogItem(
    val name: String = ""
)
