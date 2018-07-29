package io.cyanlab.wordlist.activities

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences

import android.os.Environment
import java.util.Comparator
import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList
import java.util.Arrays

import androidx.appcompat.app.AlertDialog


class FileDialog(internal var context: Context) : AlertDialog(context) {

    internal var dir: File
    internal var files: Array<String>
    internal var builder: AlertDialog.Builder
    internal var dialog: AlertDialog
    internal var filenameFilter: FilenameFilter
    //PluginProperties plugin_props;
    internal var fileDialogDepends: FileDialogDepends

    /*
     * Определяется реакция на нажатие кнопки на диалоге:
     * – если выбран <..> – переходим
     *    на уровень вверх и вызываем openFileDialog
     * – если выбрана папка – переходим
     *    в папку и вызываем openFileDialog
     * – если выбран файл – сохраняем текущий
     *    путь в настройки, вызываем fileSelected,
     * – если нажата отмена – закрываем диалог
     */

    private val listenerFileDialog = DialogInterface.OnClickListener { dialog, which ->
        dialog.dismiss()

        if (files[which] == "..") {//НАЖАТО НА <..>
            dir = File(dir.parent)
        } else
            dir = File(CURRENT_PATH + "/" + files[which])
        if (dir.isFile) { //ВЫБРАН ФАЙЛ
            val ed = context.getSharedPreferences(
                    "preferences", Context.MODE_PRIVATE).edit()
            ed.putString("pref_current_path", CURRENT_PATH)
            ed.apply()
            fileSelected(dir)
        } else if (dir.isDirectory) { //ВЫБРАНА ПАПКА
            if (files[which] != "..")
                dir = File(CURRENT_PATH + "/" + files[which])
            CURRENT_PATH = dir.toString()
            openFileDialog(fileDialogDepends)
        }
    }


    init {

        filenameFilter = FilenameFilter { directory, fileName ->
            val file = File(directory, fileName)
            if (file.isDirectory) {
                true
            } else {
                if (fileName.endsWith(".pdf")) {
                    true
                } else {
                    false
                }
            }
        }
    }

    /*
     * Вызов диалога открытия файла.
     * @param fileDialogDepends – объект, ссылка
     * на который передается из вызывающей activity для
     * взаимосвязи с ней.
     */

    fun openFileDialog(fileDialogDepends: FileDialogDepends) {
        this.fileDialogDepends = fileDialogDepends
        openFileDialog()
    }

    /*
     * Внутренняя процедура для рекурсивного
     * переоткрытия диалога файла.
     */

    private fun openFileDialog() {
        builder = AlertDialog.Builder(context)

        dir = File(CURRENT_PATH)
        //builder.setTitle(R.string.select_file);
        files = concatAll(dirs(dir), *files(dir, filenameFilter))
        builder.setItems(files, listenerFileDialog)
        builder.setNegativeButton(android.R.string.cancel, null)

        dialog = builder.create()
        dialog.show()
    }

    /*
     * Файл выбран.
     * @param file – выбранный файл.
     */

    fun fileSelected(file: File) {

        //ЗДЕСЬ ПИШЕМ ПРОЦЕДУРЫ, КОТОРЫЕ РЕАЛИЗУЮТСЯ, КОГДА
        //ФАЙЛ ВЫБРАН. ТО ЕСТЬ ОТКРЫТИЕ ФАЙЛА, ОБРАБОТКА,
        //КОПИРОВАНИЕ И ТАК ДАЛЕЕ.

        //Здесь проводим взаимосвязь с вызывающей activity.
        fileDialogDepends.refresh()

    }

    //Теперь в основной activity определяем класс для осуществления взаимосвязи с диалогом открытия файла:

    inner class FileDialogDepends {
        fun refresh(): FileDialogDepends {
            refresh()
            return this
        }
    }

    companion object {

        var FILENAME_FILTER = "(?i)^feeder\\-.*?\\.html$"
        var CURRENT_PATH = "/sdcard"

        var ROOT_PATH = Environment.getExternalStorageDirectory().path

        /*
     * Выдает отсортированный список папок из папки path.
     * 1) Игнорируются папки, начинающиеся с точки.
     * 2) В начало списка помещается пункт "..".
     * 3) Сортировка осуществляется в предпоследней строке, для
     * регистронезависимого варианта необходимо определить
     * соответствующий компаратор.
     */

        fun dirs(path: File): Array<String> {
            val files = ArrayList<String>()

            if (path.toString() != ROOT_PATH) files.add("..")

            for (a in path.listFiles()) {
                if (a.isDirectory && !a.name.toString().startsWith("."))
                    files.add(a.name.toString() + "/")
            }
            val res = files.toTypedArray()
            Arrays.sort(res, SortedByName())
            return res
        }

        /*
     * Выдает отсортированный список файлов из папки path,
     * отфильтрованный в соответствии с фильтром filter.
     * Сортировка осуществляется в предпоследней строке, для
     * регистронезависимого варианта необходимо определить
     * соответствующий компаратор.
     */

        fun files(path: File,
                  filter: FilenameFilter): Array<String> {
            val files = ArrayList<String>()

            for (a in path.listFiles(filter))
                if (a.isFile) files.add(a.name.toString())

            val res = files.toTypedArray()
            Arrays.sort(res, SortedByName())
            return res
        }

        /*
     * Объединение массивов.
     * @param first
     * @param rest
     * @return
     */

        fun <T> concatAll(first: Array<T>?, vararg rest: Array<T>): Array<T> {
            var totalLength = 0
            if (first != null) totalLength = first.size
            for (array in rest)
                if (array != null) totalLength += array.size
            val result = Arrays.copyOf(first!!, totalLength)
            var offset = first.size
            for (array in rest) {
                if (array != null) {
                    System.arraycopy(array, 0,
                            result, offset, array.size)
                    offset += array.size
                }
            }
            return result
        }
    }

    inner class SortedByName : Comparator<String> {
        override fun compare(p0: String?, p1: String?): Int {
            var str1 = p0 as String
            var str2 = p1 as String
            str1 = str1.toUpperCase()
            str2 = str2.toUpperCase()
            return str1.compareTo(str2)
        }
    }
}
