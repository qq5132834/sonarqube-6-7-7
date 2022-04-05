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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.annotation.concurrent.Immutable;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.ContextException;
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
    LOGGER.info("ScannerReportWriter.writeMetadata.写入元数据");
    Protobuf.write(metadata, fileStructure.metadataFile());
    return fileStructure.metadataFile();
  }

  public File writeActiveRules(Iterable<ScannerReport.ActiveRule> activeRules) {
    LOGGER.info("ScannerReportWriter.writeActiveRules.写入有效规则");
    Protobuf.writeStream(activeRules, fileStructure.activeRules(), false);
    return fileStructure.metadataFile();
  }

  public File writeComponent(ScannerReport.Component component) {
    LOGGER.info("ScannerReportWriter.writeComponent.写入组件");
    File file = fileStructure.fileFor(FileStructure.Domain.COMPONENT, component.getRef());
    Protobuf.write(component, file);
    return file;
  }

  public File writeComponentIssues(int componentRef, Iterable<ScannerReport.Issue> issues) {
    LOGGER.info("ScannerReportWriter.writeComponentIssues.写入issue");
    File file = fileStructure.fileFor(FileStructure.Domain.ISSUES, componentRef);
    Protobuf.writeStream(issues, file, false);
    return file;
  }

  public void appendComponentIssue(int componentRef, ScannerReport.Issue issue) {
    LOGGER.info("ScannerReportWriter.appendComponentIssue.写入组件issue");
    File file = fileStructure.fileFor(FileStructure.Domain.ISSUES, componentRef);
    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file, true))) {
      issue.writeDelimitedTo(out);
    } catch (Exception e) {
      throw ContextException.of("Unable to write issue", e).addContext("file", file);
    }
  }

  public File writeComponentMeasures(int componentRef, Iterable<ScannerReport.Measure> measures) {
    LOGGER.info("ScannerReportWriter.writeComponentMeasures.写入组件measure");
    File file = fileStructure.fileFor(FileStructure.Domain.MEASURES, componentRef);
    Protobuf.writeStream(measures, file, false);
    return file;
  }

  public File writeComponentChangesets(ScannerReport.Changesets changesets) {
    LOGGER.info("ScannerReportWriter.writeComponentChangesets.写入组件Changeset");
    File file = fileStructure.fileFor(FileStructure.Domain.CHANGESETS, changesets.getComponentRef());
    Protobuf.write(changesets, file);
    return file;
  }

  public File writeComponentDuplications(int componentRef, Iterable<ScannerReport.Duplication> duplications) {
    LOGGER.info("ScannerReportWriter.ComponentDuplications.写入组件Duplications");
    File file = fileStructure.fileFor(FileStructure.Domain.DUPLICATIONS, componentRef);
    Protobuf.writeStream(duplications, file, false);
    return file;
  }

  public File writeCpdTextBlocks(int componentRef, Iterable<ScannerReport.CpdTextBlock> blocks) {
    LOGGER.info("ScannerReportWriter.writeCpdTextBlocks.写入CpdTextBlocks");
    File file = fileStructure.fileFor(FileStructure.Domain.CPD_TEXT_BLOCKS, componentRef);
    Protobuf.writeStream(blocks, file, false);
    return file;
  }

  public File writeComponentSymbols(int componentRef, Iterable<ScannerReport.Symbol> symbols) {
    LOGGER.info("ScannerReportWriter.writeComponentSymbols.写入ComponentSymbols");
    File file = fileStructure.fileFor(FileStructure.Domain.SYMBOLS, componentRef);
    Protobuf.writeStream(symbols, file, false);
    return file;
  }

  public File writeComponentSyntaxHighlighting(int componentRef, Iterable<ScannerReport.SyntaxHighlightingRule> syntaxHighlightingRules) {
    LOGGER.info("ScannerReportWriter.writeComponentSyntaxHighlighting.写入高亮");
    File file = fileStructure.fileFor(FileStructure.Domain.SYNTAX_HIGHLIGHTINGS, componentRef);
    Protobuf.writeStream(syntaxHighlightingRules, file, false);
    return file;
  }

  public File writeComponentCoverage(int componentRef, Iterable<ScannerReport.LineCoverage> coverageList) {
    LOGGER.info("ScannerReportWriter.writeComponentCoverage.写入覆盖率");
    File file = fileStructure.fileFor(FileStructure.Domain.COVERAGES, componentRef);
    Protobuf.writeStream(coverageList, file, false);
    return file;
  }

  public File writeTests(int componentRef, Iterable<ScannerReport.Test> tests) {
    LOGGER.info("ScannerReportWriter.writeTests");
    File file = fileStructure.fileFor(FileStructure.Domain.TESTS, componentRef);
    Protobuf.writeStream(tests, file, false);
    return file;
  }

  public File writeCoverageDetails(int componentRef, Iterable<ScannerReport.CoverageDetail> tests) {
    LOGGER.info("ScannerReportWriter.writeCoverageDetails.覆盖率详情");
    File file = fileStructure.fileFor(FileStructure.Domain.COVERAGE_DETAILS, componentRef);
    Protobuf.writeStream(tests, file, false);
    return file;
  }

  public File writeContextProperties(Iterable<ScannerReport.ContextProperty> properties) {
    LOGGER.info("ScannerReportWriter.writeContextProperties.");
    File file = fileStructure.contextProperties();
    Protobuf.writeStream(properties, file, false);
    return file;
  }

  public File getSourceFile(int componentRef) {
    LOGGER.info("ScannerReportWriter.getSourceFile.");
    return fileStructure.fileFor(FileStructure.Domain.SOURCE, componentRef);
  }

}
