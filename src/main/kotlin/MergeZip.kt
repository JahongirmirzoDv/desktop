@file:OptIn(DelicateCoroutinesApi::class)

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun mergeZip(listFile: List<File>, zipFilePath: String): String {
//    val filesToZip = listOf("file1.txt", "file2.txt", "file3.txt")
//    val zipFilePath = "/path/to/your/directory/mergedFiles.zip" // Specify the full path here

    mergeFilesToZip(listFile, zipFilePath)

    println("Files merged successfully to $zipFilePath")
    return zipFilePath
}

fun mergeFilesToZip(filesToZip: List<File>, zipFilePath: String) {
    ZipOutputStream(FileOutputStream(zipFilePath)).use { zipOutputStream ->
        filesToZip.forEach { fileName ->
            val file = File(fileName.absolutePath)
            zipOutputStream.putNextEntry(ZipEntry(file.name))

            file.inputStream().use { fileInputStream ->
                fileInputStream.copyTo(zipOutputStream)
            }

            zipOutputStream.closeEntry()
        }
    }
}