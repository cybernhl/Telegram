import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileWriter

abstract class CollectI18nTask : SourceTask() {

    @get:Input
    lateinit var langKtFile: Provider<File>

    @TaskAction
    fun execute() {
        val langKtFile = langKtFile.get()
        if (!langKtFile.exists()) {
            error("langKtFile not found: ${langKtFile.absolutePath}")
        }
        if (!langKtFile.isFile) {
            error("langKtFile is not a file: ${langKtFile.absolutePath}")
        }

        val appendKeys = mutableMapOf<String, String>()
        source.forEach {
            collectI18nFile(appendKeys, it)
        }

        if (appendKeys.isNotEmpty()) {
            // remove exist keys
            langKtFile.forEachLine { line ->
                if (line.startsWith("const val KEY_")) {
                    val key = line.substringAfter("const val KEY_").substringBefore(" =").intern()
                    appendKeys.remove(key)
                }
            }

            // append new keys
            FileWriter(langKtFile, true).use { writer ->
                writer.appendLine()
                writer.appendLine()
                appendKeys.forEach {
                    writer.append("const val KEY_${it.key} = \"${it.value}\"\n")
                }
            }
        }
    }

    private fun collectI18nFile(appendKeys: MutableMap<String, String>, file: File) {
        val isJava = file.extension == "java"
        val langKtPackage = if (isJava) {
            "org.telegram.messenger.utils.LangMultiExtKt"
        } else {
            "org.telegram.messenger.utils"
        }

        var found = false

        val tempFile = File.createTempFile("${file.name}.temp", null)
        tempFile.bufferedWriter().use { writer ->
            file.forEachLine { line ->
                if (line.contains(GET_STRING_FUNC)) {
                    // LocaleController.getString("Register", R.string.Register) -> "Register", R.string.Register
                    val start = line.indexOf(GET_STRING_FUNC) + GET_STRING_FUNC.length
                    val end = line.indexOfFunEnd(start)
                    if (start >= end) return@forEachLine

                    // "Register", R.string.Register -> ["Register", R.string.Register]
                    val content = line.substring(start, end).split(',')
                    val newLine = when (content.size) {
                        1 -> {
                            val key = content[0].trim().intern()
                            if (key.startsWith('"') && key.endsWith('"')) {
                                found = true

                                val safeKey = key.substring(1, key.length - 1) // remove "
                                appendKeys[safeKey.uppercase()] = safeKey

                                val constKey = "KEY_${safeKey.uppercase()}"
                                "${line.substring(0, start)}${langKtPackage}.${constKey}${line.substring(end)}"
                            } else if (key.startsWith("R.string")) {
                                found = true

                                val rKey = key.substringAfter("R.string.").trim()
                                appendKeys[rKey.uppercase()] = rKey

                                val constKey = "KEY_${rKey.uppercase()}"
                                "${line.substring(0, start)}${langKtPackage}.${constKey}, $key${line.substring(end)}"
                            } else {
                                // like: LocaleController.getString(getTextString()) -> getTextString()
                                // pass modify
                                line
                            }
                        }
                        2 -> {
                            val key = content[0].trim().intern()
                            val rValue = content[1].trim().intern()
                            if (key.startsWith('"') && key.endsWith('"')) {
                                found = true

                                val safeKey = key.substring(1, key.length - 1) // remove "
                                appendKeys[safeKey.uppercase()] = safeKey

                                val constKey = "KEY_${safeKey.uppercase()}"
                                "${line.substring(0, start)}${langKtPackage}.${constKey}, ${rValue}${line.substring(end)}"
                            } else {
                                // key is const, pass modify
                                line
                            }
                        }
                        else -> {
                            println("unknown string content: $content")
                            line
                        }
                    }

                    writer.appendLine(newLine)
                } else {
                    writer.appendLine(line)
                }
            }
        }
        if (found) {
            file.delete()
            tempFile.renameTo(file)
        } else {
            tempFile.delete()
        }
    }

    companion object {
        private const val GET_STRING_FUNC = "LocaleController.getString("
    }
}