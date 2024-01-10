import java.awt.*
import java.awt.print.*
import java.awt.print.Printable.NO_SUCH_PAGE
import java.awt.print.Printable.PAGE_EXISTS
import java.io.File
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.MediaSize
import javax.print.attribute.standard.MediaSizeName
import javax.swing.*

class PrintImagePage(private val image: Image) : Printable {
    override
    fun print(graphics: Graphics, pageFormat: PageFormat, pageIndex: Int): Int {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE
        }

        val graphics2D = graphics as Graphics2D
        graphics2D.drawImage(image, 0, 0, null)
        return PAGE_EXISTS
    }
}





