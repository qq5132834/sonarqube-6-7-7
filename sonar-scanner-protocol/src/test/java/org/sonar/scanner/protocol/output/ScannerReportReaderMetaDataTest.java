package org.sonar.scanner.protocol.output;

import org.junit.Test;
import org.sonar.core.util.Protobuf;

import java.io.File;

public class ScannerReportReaderMetaDataTest {

    /***
     * 读取metadata.bp文件内容
     * @throws Exception
     */
    @Test
    public void readMetadataTest() throws Exception {
        String currentPath = System.getProperty("user.dir");
        String metadataFilePath = currentPath + "\\src\\test\\java\\org\\sonar\\scanner\\protocol\\output" + File.separator + "db" + File.separator + "metadata.pb";
        File file = new File(metadataFilePath);
        ScannerReport.Metadata metadata = Protobuf.read(file, ScannerReport.Metadata.parser());
        System.out.println("");
    }

}
