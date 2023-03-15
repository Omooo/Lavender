package com.omooo.plugin.internal.cha

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory

/**
 * Author: Omooo
 * Date: 2023/3/12
 * Desc:
 */
internal class ComponentHandler(private val manifest: File) : DefaultHandler() {

    val applications = mutableSetOf<String>()
    val activities = mutableSetOf<String>()
    val services = mutableSetOf<String>()
    val providers = mutableSetOf<String>()
    val receivers = mutableSetOf<String>()

    /**
     * 获取组件列表
     *
     * @return {"com.xxx.MyApplication", "com.xxx.MainActivity"}
     */
    fun getComponentSet(): Set<String> {
        SAXParserFactory.newInstance().newSAXParser().parse(manifest, this)
        return applications + activities + services + receivers
    }

    override fun startElement(
        uri: String,
        localName: String,
        qName: String,
        attributes: Attributes
    ) {
        val name: String = attributes.getValue(ATTR_NAME) ?: return

        when (qName) {
            "application" -> {
                applications.add(name)
            }
            "activity" -> {
                activities.add(name)
            }
            "service" -> {
                services.add(name)
            }
            "provider" -> {
                providers.add(name)
            }
            "receiver" -> {
                receivers.add(name)
            }
        }
    }

}

private const val ATTR_NAME = "android:name"