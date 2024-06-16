/*
 * Created 2018-4-16 12:11:22
 */
package cn.com.yting.kxy.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Azige
 */
public final class ResourceLoaderUtils {

    private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    private ResourceLoaderUtils() {
    }

    /**
     * 将一个包含资源对象的列表转换为 id -> 资源对象 的映射。
     * 如果列表中包含相同 id 的资源对象会导致异常。
     *
     * @param <T>
     * @param list
     * @return
     */
    public static <T extends Resource> Map<Long, T> convertListToMap(List<T> list) {
        return list.stream()
            .collect(Collectors.toMap(Resource::getId, Function.identity(), (a, b) -> {
                throw new IllegalStateException("id重复，id=" + a.getId() + ", type=" + a.getClass().getName());
            }));
    }

    /**
     * 从输入流中提取以 XML 描述的资源。
     * XML 的格式是由 ExcelParser 定义的
     *
     * @param <T>
     * @param inputStream
     * @param dataType 映射到 XML 中的 &lt;data&gt; 节点的类型
     * @return
     */
    public static <T extends Resource> List<T> extractDataList(InputStream inputStream, Class<T> dataType) {
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);
            Node root = document.getFirstChild();
            NodeList childNodes = root.getChildNodes();
            List<T> dataList = new ArrayList<>();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                if (!Objects.equals(node.getNodeName(), "data")) {
                    continue;
                }
                dataList.add(JAXB.unmarshal(new DOMSource(node), dataType));
            }
            return dataList;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 相当于 {@code convertListToMap(extractDataList(inputStream, dataType)) }
     *
     * @param <T>
     * @param inputStream
     * @param dataType
     * @return
     */
    public static <T extends Resource> Map<Long, T> extractDataMap(InputStream inputStream, Class<T> dataType) {
        return convertListToMap(extractDataList(inputStream, dataType));
    }
}
