package org.sonar.scanner.protocol.output;

import org.junit.Before;
import org.junit.Test;
import org.sonar.core.util.CloseableIterator;

import java.io.File;

public class ScannerReportReaderMetaDataTest {

    private ScannerReportReader scannerReportReader;

    @Before
    public void readReport(){
        String currentPath = System.getProperty("user.dir");
        String dir = currentPath + "\\src\\test\\java\\org\\sonar\\scanner\\protocol\\output" + File.separator + "db" + File.separator;
        scannerReportReader = new ScannerReportReader(new File(dir));
    }

    /***
     * 读取metadata.bp文件内容
     */
    @Test
    public void readMetadataTest() throws Exception {
        ScannerReport.Metadata metadata = scannerReportReader.readMetadata();
        System.out.println("");
    }

    /***
     *
     */
    @Test
    public void readComponentTest(){
        ScannerReport.Component component = scannerReportReader.readComponent(1);
        System.out.println("");
    }

    @Test
    public void readActiveRulesTest(){
        CloseableIterator<ScannerReport.ActiveRule> activeRuleCloseableIterator = this.scannerReportReader.readActiveRules();
        while (activeRuleCloseableIterator.hasNext()){
            ScannerReport.ActiveRule activeRule = activeRuleCloseableIterator.next();
            System.out.println(activeRule.getRuleRepository() + "/" + activeRule.getRuleKey() + "/" + activeRule.getSeverity().name());
        }
        System.out.println();
    }

    @Test
    public void readContextPropsTest(){
        CloseableIterator<ScannerReport.ContextProperty> contextPropertyCloseableIterator = this.scannerReportReader.readContextProperties();
        while (contextPropertyCloseableIterator.hasNext()){
            ScannerReport.ContextProperty contextProperty = contextPropertyCloseableIterator.next();
        }
        System.out.println();
    }

    @Test
    public void readCoveragesTest(){
        CloseableIterator<ScannerReport.LineCoverage> lineCoverageCloseableIterator = this.scannerReportReader.readComponentCoverage(2);
        while (lineCoverageCloseableIterator.hasNext()){
            ScannerReport.LineCoverage lineCoverage = lineCoverageCloseableIterator.next();
        }
        System.out.println();
    }

    /***
     * 高亮
     */
    @Test
    public void readHighlightingTest(){
        CloseableIterator<ScannerReport.SyntaxHighlightingRule> highlightingRuleCloseableIterator = this.scannerReportReader.readComponentSyntaxHighlighting(3);
        while (highlightingRuleCloseableIterator.hasNext()){
            ScannerReport.SyntaxHighlightingRule syntaxHighlightingRule = highlightingRuleCloseableIterator.next();
            System.out.println(syntaxHighlightingRule.getType() + "," +syntaxHighlightingRule.getRange().toString().replaceAll("\n", ", "));
        }
    }

    /***
     * 问题
     */
    @Test
    public void readIssueTest(){
        CloseableIterator<ScannerReport.Issue> issueCloseableIterator = this.scannerReportReader.readComponentIssues(3);
        while (issueCloseableIterator.hasNext()){
            ScannerReport.Issue issue = issueCloseableIterator.next();
            issue.getGap();
            issue.getSeverity();
            issue.getMsg();
            issue.getRuleKey();
            issue.getTextRange();
            issue.getRuleRepository();
            System.out.println();
        }
    }

}
