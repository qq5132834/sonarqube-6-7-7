/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.protocol.output;

import java.io.*;
import javax.annotation.concurrent.Immutable;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.ContextException;
import org.sonar.core.util.FileUtils;
import org.sonar.core.util.Protobuf;

@Immutable
public class ScannerReportWriter {
  private final static Logger LOGGER = Loggers.get(ScannerReportWriter.class);
  private final FileStructure fileStructure;

  public ScannerReportWriter(File dir) {
    LOGGER.info("ScannerReportWriter.扫描报告文档路径:{}", dir != null && dir.exists()?dir.getAbsolutePath():"");
    if (!dir.exists() && !dir.mkdirs()) {
      throw new IllegalStateException("Unable to create directory: " + dir);
    }
    this.fileStructure = new FileStructure(dir);
  }

  public FileStructure getFileStructure() {
    return fileStructure;
  }

  public boolean hasComponentData(FileStructure.Domain domain, int componentRef) {
    File file = fileStructure.fileFor(domain, componentRef);
    return file.exists() && file.isFile();
  }

  /**
   * Metadata is mandatory
   */
  public File writeMetadata(ScannerReport.Metadata metadata) {
    Protobuf.write(metadata, fileStructure.metadataFile());
    File file = fileStructure.metadataFile();
    LOGGER.info("ScannerReportWriter.writeMetadata.写入元数据.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeActiveRules(Iterable<ScannerReport.ActiveRule> activeRules) {
    Protobuf.writeStream(activeRules, fileStructure.activeRules(), false);
    File file = fileStructure.metadataFile();
    LOGGER.info("ScannerReportWriter.writeActiveRules.写入有效规则.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponent(ScannerReport.Component component) {
    File file = fileStructure.fileFor(FileStructure.Domain.COMPONENT, component.getRef());
    Protobuf.write(component, file);
    LOGGER.info("ScannerReportWriter.writeComponent.写入组件.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentIssues(int componentRef, Iterable<ScannerReport.Issue> issues) {
    File file = fileStructure.fileFor(FileStructure.Domain.ISSUES, componentRef);
    Protobuf.writeStream(issues, file, false);
    LOGGER.info("ScannerReportWriter.writeComponentIssues.写入issue.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  /***
   * 向自定义custom_data.json文件中写入数据
   * @param jsonData
   * @return
   */
  public File writeCustomData(String jsonData){
    File file = fileStructure.customData();
    try {
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
      bufferedWriter.write(jsonData);
      bufferedWriter.flush();
      bufferedWriter.close();
    }catch (Exception e){
      e.printStackTrace();
    }
    LOGGER.info("ScannerReportWriter.writeCustomData.写入自定义json数据.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public void appendComponentIssue(int componentRef, ScannerReport.Issue issue) {
    File file = fileStructure.fileFor(FileStructure.Domain.ISSUES, componentRef);
    LOGGER.info("ScannerReportWriter.appendComponentIssue.写入组件issue.file路径:{}", file!=null?file.getAbsolutePath():"");
    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file, true))) {
      issue.writeDelimitedTo(out);
    } catch (Exception e) {
      throw ContextException.of("Unable to write issue", e).addContext("file", file);
    }
  }

  public File writeComponentMeasures(int componentRef, Iterable<ScannerReport.Measure> measures) {
    File file = fileStructure.fileFor(FileStructure.Domain.MEASURES, componentRef);
    Protobuf.writeStream(measures, file, false);
    LOGGER.info("ScannerReportWriter.writeComponentMeasures.写入组件measure.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentChangesets(ScannerReport.Changesets changesets) {
    File file = fileStructure.fileFor(FileStructure.Domain.CHANGESETS, changesets.getComponentRef());
    Protobuf.write(changesets, file);
    LOGGER.info("ScannerReportWriter.writeComponentChangesets.写入组件Changeset.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentDuplications(int componentRef, Iterable<ScannerReport.Duplication> duplications) {
    File file = fileStructure.fileFor(FileStructure.Domain.DUPLICATIONS, componentRef);
    Protobuf.writeStream(duplications, file, false);
    LOGGER.info("ScannerReportWriter.ComponentDuplications.写入组件Duplications.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeCpdTextBlocks(int componentRef, Iterable<ScannerReport.CpdTextBlock> blocks) {
    File file = fileStructure.fileFor(FileStructure.Domain.CPD_TEXT_BLOCKS, componentRef);
    Protobuf.writeStream(blocks, file, false);
    LOGGER.info("ScannerReportWriter.writeCpdTextBlocks.写入CpdTextBlocks.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentSymbols(int componentRef, Iterable<ScannerReport.Symbol> symbols) {
    File file = fileStructure.fileFor(FileStructure.Domain.SYMBOLS, componentRef);
    Protobuf.writeStream(symbols, file, false);
    LOGGER.info("ScannerReportWriter.writeComponentSymbols.写入ComponentSymbols.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentSyntaxHighlighting(int componentRef, Iterable<ScannerReport.SyntaxHighlightingRule> syntaxHighlightingRules) {
    File file = fileStructure.fileFor(FileStructure.Domain.SYNTAX_HIGHLIGHTINGS, componentRef);
    Protobuf.writeStream(syntaxHighlightingRules, file, false);
    LOGGER.info("ScannerReportWriter.writeComponentSyntaxHighlighting.写入高亮.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeComponentCoverage(int componentRef, Iterable<ScannerReport.LineCoverage> coverageList) {
    File file = fileStructure.fileFor(FileStructure.Domain.COVERAGES, componentRef);
    Protobuf.writeStream(coverageList, file, false);
    LOGGER.info("ScannerReportWriter.writeComponentCoverage.写入覆盖率.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeTests(int componentRef, Iterable<ScannerReport.Test> tests) {
    File file = fileStructure.fileFor(FileStructure.Domain.TESTS, componentRef);
    Protobuf.writeStream(tests, file, false);
    LOGGER.info("ScannerReportWriter.writeTests.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeCoverageDetails(int componentRef, Iterable<ScannerReport.CoverageDetail> tests) {
    File file = fileStructure.fileFor(FileStructure.Domain.COVERAGE_DETAILS, componentRef);
    Protobuf.writeStream(tests, file, false);
    LOGGER.info("ScannerReportWriter.writeCoverageDetails.覆盖率详情.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File writeContextProperties(Iterable<ScannerReport.ContextProperty> properties) {
    File file = fileStructure.contextProperties();
    Protobuf.writeStream(properties, file, false);
    LOGGER.info("ScannerReportWriter.writeContextProperties.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

  public File getSourceFile(int componentRef) {
    File file = fileStructure.fileFor(FileStructure.Domain.SOURCE, componentRef);
    LOGGER.info("ScannerReportWriter.getSourceFile.file路径:{}", file!=null?file.getAbsolutePath():"");
    return file;
  }

}
