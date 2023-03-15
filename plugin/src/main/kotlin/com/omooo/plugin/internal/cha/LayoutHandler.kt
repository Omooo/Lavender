package com.omooo.plugin.internal.cha

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory

/**
 * Author: Omooo
 * Date: 2023/3/13
 * Desc:
 */
internal class LayoutHandler(private val layoutFile: File) : DefaultHandler() {

    private val views = mutableSetOf<String>()

    fun getViews(): Set<String> {
        SAXParserFactory.newInstance().newSAXParser().parse(layoutFile, this)
        return views
    }

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        views += qName
    }

}