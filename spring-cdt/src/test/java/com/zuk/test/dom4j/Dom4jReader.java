package com.zuk.test.dom4j;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Dom4jReader {

    private static String XML = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring-cdt\\src\\test\\resources\\test.xml";

    public static void main(String[] args) throws DocumentException, IOException {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new File(XML));
            writeXml(document,"user","name");
            System.out.println("该元素的Attribute: " + getXmlAttribute(document,"user","age"));
            System.out.println("该元素的Text: " + getXmlElementText(document,"user"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * xml属性
     *
     * @param document  文档
     * @param element   元素
     * @param attribute 属性
     * @return {@link String}
     */
    public static String getXmlAttribute(Document document,String element,String attribute){
        Element root = document.getRootElement();
        Element child = root.element(element);
        Attribute attributeValue = child.attribute(attribute);
        return attributeValue.getText();
    }

    /**
     * xml元素文本
     * xml属性
     *
     * @param document 文档
     * @param element  元素
     * @return {@link String}
     */
    public static String getXmlElementText(Document document,String element){
        Element root = document.getRootElement();
        Element child = root.element(element);
        return child.getText();
    }

    /**
     * 编写xml
     *
     * @param document 文档
     * @param element  元素
     * @param value    价值
     */
    public static void writeXml(Document document,String element,String value){

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");

        Element root = document.getRootElement();
        Element user = root.addElement(element);

        Element values = user.addElement(value);
        user.setText("user的text");
        user.addAttribute("age","16");
        values.setText("小明");
        try{
            File file = new File(XML);
            XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), format);
            xmlWriter.write(user);
            xmlWriter.close();
//            FileWriter fileWrite = new FileWriter(new File(XML));
//            XMLWriter xmlWriter = new XMLWriter(fileWrite, format);
//            xmlWriter.write(user);
//            xmlWriter.flush();
//            xmlWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




}
