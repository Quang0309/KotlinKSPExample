package com.example.processor

import com.example.annotation.GenerateActivityExtensions
import com.google.devtools.ksp.outerType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import java.io.OutputStream
import java.io.Serializable
import java.lang.System.Logger
import kotlin.reflect.KType

data class NavigationParam(
    val fieldName: String,
    val type: KSType,
    val variableName: String
)

class ActivityExtensionProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {

        resolver.getSymbolsWithAnnotation(GenerateActivityExtensions::class.qualifiedName ?: "").forEach { symbol ->
            if (symbol is KSClassDeclaration) {
                val className = symbol.simpleName.asString()


                // create a file
                val file: OutputStream = codeGenerator.createNewFile(
                    // Make sure to associate the generated file with sources to keep/maintain it across incremental builds.
                    // Learn more about incremental processing in KSP from the official docs:
                    // https://kotlinlang.org/docs/ksp-incremental.html
                    dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                    packageName = "com.example.kotlinksp",
                    fileName = "${className}GeneratedFunctions",
                    extensionName = "kt"
                )

                // adding import
                file += "package com.example.kotlinksp\n"
                file += "import android.content.Context\n"
                file += "import android.content.Intent\n"


                val a = symbol.simpleName.asString()
                file += "fun Context.navigateTo$a(\n"



                val list = mutableListOf<NavigationParam>()
                symbol.accept(Visitor(file, list), Unit)
                file += "): Intent {\n"
                file += "    return Intent(this, $a::class.java).apply {\n"
                list.forEach {
                    file += "        putExtra(\"${it.fieldName}\", ${it.variableName})\n"
                }
                file += "    }"
                file += "}\n"

                file += "fun $a.bindArgs() {\n"
                list.forEach {
                    file += generateGetFunction(it.variableName, it.fieldName, it.type)
                    file += "\n"
                }
                file += "}"

                file.close()
            }
        }

        return emptyList()
    }

    private fun generateGetFunction(variableName: String, fieldName: String, type: KSType) : String {
        val typeQualifiedName = type.declaration.qualifiedName?.asString() ?: error("Invalid property type $type")
        return "    " + when (typeQualifiedName) {

            Byte::class.qualifiedName -> "$variableName = intent.getByteExtra(\"$fieldName\", $variableName)"
            Char::class.qualifiedName -> "$variableName = intent.getCharExtra(\"$fieldName\", $variableName)"
            Short::class.qualifiedName -> "$variableName = intent.getShortExtra(\"$fieldName\", $variableName)"
            Float::class.qualifiedName -> "$variableName = intent.getFloatExtra(\"$fieldName\", $variableName)"
            Boolean::class.qualifiedName -> "$variableName = intent.getBooleanExtra(\"$fieldName\", $variableName)"
            Int::class.qualifiedName -> "$variableName = intent.getIntExtra(\"$fieldName\", $variableName)"
            Long::class.qualifiedName -> "$variableName = intent.getLongExtra(\"$fieldName\", $variableName)"
            Double::class.qualifiedName -> "$variableName = intent.getDoubleExtra(\"$fieldName\", $variableName)"
            String::class.qualifiedName -> "$variableName = intent.getStringExtra(\"$fieldName\")"
            "kotlin.collections.ArrayList" -> genCodeForArrayList(variableName, fieldName, type)

            else -> {
                val isParcelable = (type.declaration as KSClassDeclaration).superTypes.any {
                     it.resolve().declaration.qualifiedName?.asString() == "android.os.Parcelable"
                }
                if (isParcelable) {
                    "$variableName = intent.getParcelableExtra(\"$fieldName\")"
                } else {

                }
            }
        }
    }

    private fun genCodeForArrayList(variableName: String, fieldName: String, type: KSType): String {
        val typeOfArray = type.arguments.firstOrNull()
        return when (typeOfArray?.type?.resolve()?.declaration?.qualifiedName?.asString()) {
            Int::class.qualifiedName -> "$variableName = intent.getIntegerArrayListExtra(\"$fieldName\")!!"
            String::class.qualifiedName -> "$variableName = intent.getStringArrayListExtra(\"$fieldName\")!!"
            CharSequence::class.qualifiedName -> "$variableName = intent.getStringArrayListExtra(\"$fieldName\")!!"
            else -> "$variableName = intent.getParcelableArrayListExtra(\"$fieldName\")!!"
        }
    }


    inner class Visitor(private val file: OutputStream, private val list: MutableList<NavigationParam>) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            super.visitClassDeclaration(classDeclaration, data)
            if (classDeclaration.classKind != ClassKind.CLASS) {
                error("Only interface can be annotated with @GenerateActivityExtensions $classDeclaration")
                return
            }

            val properties: Sequence<KSPropertyDeclaration> = classDeclaration.getAllProperties()
                .filter {
                    it.validate { _, _ ->
                        it.annotations.any { it.shortName.asString() ==  "NavigationParameter" }
                    }
                }
            properties.forEach {
                val annotation = it.annotations.first { it.shortName.asString() ==  "NavigationParameter" }
                val fieldName = annotation.arguments.first { arg -> arg.name?.asString() == "name" }.value as String
                val type = it.type.resolve()
                list.add(NavigationParam(fieldName, type ,it.simpleName.asString()))
                visitPropertyDeclaration(it, Unit)
            }
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
            // Generating argument name.
            val argumentName = property.simpleName.asString()
            file += "    $argumentName: "


            // Generating argument type.
            val resolvedType = property.type.resolve()
            file += resolvedType.declaration.qualifiedName?.asString() ?: run {
                error("Invalid property type $property")
                return
            }

            // Generating generic parameters if any.
            val genericArguments: List<KSTypeArgument> = property.type.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            // Handling nullability.
            file += if (resolvedType.nullability == Nullability.NULLABLE) "?" else ""

            file += ",\n"
        }

        private fun visitTypeArguments(typeArguments: List<KSTypeArgument>) {
            if (typeArguments.isNotEmpty()) {
                file += "<"
                typeArguments.forEachIndexed { i, arg ->
                    visitTypeArgument(arg, data = Unit)
                    if (i < typeArguments.lastIndex) file += ", "
                }
                file += ">"
            }
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {

            when (val variance: Variance = typeArgument.variance) {
                // <*>
                Variance.STAR -> {
                    file += "*"
                    return
                }
                // <out ...>, <in ...>
                Variance.COVARIANT, Variance.CONTRAVARIANT -> {
                    file += variance.label
                    file += " "
                }
                Variance.INVARIANT -> {
                    // Do nothing.
                }
            }
            val resolvedType = typeArgument.type?.resolve()
            file += resolvedType?.declaration?.qualifiedName?.asString() ?: run {
                error("Invalid type argument $typeArgument")
                return
            }

            // Generating nested generic parameters if any.
            val genericArguments: List<KSTypeArgument> = typeArgument.type?.element?.typeArguments ?: emptyList()
            visitTypeArguments(genericArguments)

            // Handling nullability.
            file += if (resolvedType?.nullability == Nullability.NULLABLE) "?" else ""
        }
    }


    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }
}



