package com.example.kotlinksp

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.annotation.GenerateActivityExtensions
import com.example.annotation.NavigationParameter
import kotlinx.parcelize.Parcelize

fun function0Param() {}

fun function1Param(a: Int) {}

fun function2Param(a: Int, b: Int) {}

fun function3Param(a: Int, b: Int, c: Int) {}

fun function4Param(a: Int, b: Int, c: Int, d: Int) {}

fun function5Param(a: Int, b: Int, c: Int, d: Int, e: Int) {}

fun function6Param(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int) {}

/*abstract class NavigationParam

data class ActivityAParam(val a: Int) : NavigationParam()

val map = mutableMapOf<Class<out AppCompatActivity>, NavigationParam>()

@Parcelize
data class TestObjet(val list: List<String>): Parcelable

@GenerateActivityExtensions
class ActivityA : AppCompatActivity() {

    @NavigationParameter(name = "INT_PARAM")
    var abc: Int = 0

    @NavigationParameter(name = "STRING_PARAM")
    var xyz: String? = "haha"

    @NavigationParameter(name = "PARCELABLE_PARAM")
    var testObjet: TestObjet? = null



    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_a)
        //bindArgs()
        Log.e("QuangLog", "$abc $xyz $testObjet")

    }
}*/


/*
class InterceptOnCreateProcessor : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val a = ActivityA::class.java
        map[ActivityA::class.java] = ActivityAParam(2)
        val activitySymbol = resolver.getClassDeclarationByName("android.app.Activity") ?: return emptyList()


        val activitySubtypes = resolver.getSubtypesOf(activitySymbol.asType())

        val onCreateInterceptor = """
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                // Your interception logic here
            }
        """.trimIndent()

        val interceptedActivities = mutableListOf<KSAnnotated>()

        for (activity in activitySubtypes) {
            val annotations = activity.annotations
            for (annotation in annotations) {
                if (annotation.annotationType.resolve().declaration.qualifiedName?.asString() == "your.package.name.InterceptOnCreate") {
                    // Generate the intercepted onCreate method
                    // Add your interceptor code to the generated method
                    // You may need to handle existing onCreate methods in a more complex way
                    val modifiedActivityCode = activity.getDeclaration().let { declaration ->
                        declaration.replaceMethod("onCreate", onCreateInterceptor)
                    }

                    interceptedActivities.add(KSAnnotatedImpl(activity, annotations))
                }
            }
        }

        return interceptedActivities
    }

    // Implement other methods of the SymbolProcessor interface
}*/
