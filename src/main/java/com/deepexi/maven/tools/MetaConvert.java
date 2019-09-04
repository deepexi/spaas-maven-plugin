package com.deepexi.maven.tools;

import com.deepexi.maven.entity.UserInfo;
import org.apache.maven.plugin.MojoFailureException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @title: MetaConvert
 * @package com.deepexi.maven.tools
 * @description:
 * @author chenling
 * @date 2019/8/23 11:55
 * @since V1.0.0
 */
public class MetaConvert {

//    private static Logger log = LoggerFactory.getLogger(MetaConvert.class);
    private static JAXBContext jaxbContext;
    private static Marshaller marshaller;
    private static Unmarshaller unmarshaller;
    private static Lock lock = new ReentrantLock();


    public static JAXBContext getJAXBContext() throws JAXBException {
        if (jaxbContext == null){
            jaxbContext = JAXBContext.newInstance(UserInfo.class);
        }
        return jaxbContext;
    }

    /**
     * 将UserInfo对象序列化为XML字符串
     * @param metaData
     * @return
     * @throws Exception
     */
    public static String toXml(UserInfo metaData) throws Exception {
        StringWriter stringWriter = new StringWriter();
        lock.lock();
        getMarshaller().marshal(metaData, stringWriter);
        lock.unlock();
        return stringWriter.toString();
    }


    public static Marshaller getMarshaller() throws JAXBException {
        if (marshaller == null) {
            marshaller = getJAXBContext().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        }
        return marshaller;
    }

    /**
     * 解析XML数据(采用UTF-8字符集编码)
     * @param xmlBytes 待解析的XML数据
     * @return
     */
    public static UserInfo parseXml(byte[] xmlBytes) throws MojoFailureException {
        return parseXml(xmlBytes, "UTF-8");
    }

    /**
     * 解析XML数据
     * @param xmlBytes 待解析的XML数据
     * @param encoding 字符集编码
     * @return
     */
    public static UserInfo parseXml(byte[] xmlBytes, String encoding) throws MojoFailureException {

        UserInfo userInfo ;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(xmlBytes);
            InputStreamReader isr = new InputStreamReader(bis, encoding);
            JAXBContext jaxbContext = JAXBContext.newInstance(UserInfo.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            userInfo = (UserInfo) unmarshaller.unmarshal(isr);
        } catch (Exception e) {
//            try {
//                log.info("Original XML:" + new String(xmlBytes, encoding));
//            } catch (Exception e1) {
//                log.error(e.getMessage(), e);
//            }
            throw new MojoFailureException("用户信息格式化处理异常！");
        }

        return userInfo;
    }

}
