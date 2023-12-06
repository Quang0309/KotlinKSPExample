package com.example.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class CountInterfaceProcessor(private val logger: KSPLogger): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        /*logger.warn("hehehe")
        resolver.getAllFiles()
            .forEach { logger.warn(it.fileName + "---" + it.packageName.asString()) }*/
        val functionList = resolver.getAllFiles()
            //.filter { it.packageName.toString() == "com.example.kotlinksp" }
            .flatMap { it.declarations }
            .filterIsInstance<KSFunctionDeclaration>()

        functionList.forEach {
            if (it.parameters.size > 3) {
                logger.warn(it.qualifiedName?.asString()!!)
            }
        }
        return emptyList()
    }
}