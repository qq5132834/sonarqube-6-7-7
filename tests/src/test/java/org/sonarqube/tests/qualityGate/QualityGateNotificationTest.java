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
package org.sonarqube.tests.qualityGate;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.SonarScanner;
import java.util.Iterator;
import javax.mail.internet.MimeMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.tests.Category1Suite;
import org.sonarqube.tests.Tester;
import org.sonarqube.ws.WsProjects.CreateWsResponse.Project;
import org.sonarqube.ws.WsQualityGates;
import org.sonarqube.ws.client.PostRequest;
import org.sonarqube.ws.client.qualitygate.CreateConditionRequest;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.sonarqube.ws.WsMeasures.Measure;
import static util.ItUtils.getMeasure;
import static util.ItUtils.projectDir;

public class QualityGateNotificationTest {

  @ClassRule
  public static Orchestrator orchestrator = Category1Suite.ORCHESTRATOR;

  @Rule
  public Tester tester = new Tester(orchestrator).disableOrganizations();

  private static Wiser SMTP_SERVER;

  @BeforeClass
  public static void startSmtpServer() throws Exception {
    SMTP_SERVER = new Wiser(0);
    SMTP_SERVER.start();
  }

  @AfterClass
  public static void stopSmtpServer() throws Exception {
    if (SMTP_SERVER != null) {
      SMTP_SERVER.stop();
    }
  }

  @Test
  public void status_on_metric_variation_and_send_notifications() throws Exception {
    tester.settings().setGlobalSettings("sonar.leak.period", "previous_version");
    tester.settings().setGlobalSettings("email.smtp_host.secured", "localhost");
    tester.settings().setGlobalSettings("email.smtp_port.secured", Integer.toString(SMTP_SERVER.getServer().getPort()));

    // Create user, who will receive notifications for new violations
    tester.users().generate(u -> u.setLogin("tester").setPassword("tester").setEmail("tester@example.org"));
    // Send test email to the test user
    tester.wsClient().wsConnector().call(new PostRequest("api/emails/send")
      .setParam("to", "test@example.org")
      .setParam("message", "This is a test message from SonarQube"))
      .failIfNotSuccessful();
    // Add notifications to the test user
    tester.as("tester").wsClient().wsConnector().call(new PostRequest("api/notifications/add")
      .setParam("type", "NewAlerts")
      .setParam("channel", "EmailNotificationChannel"))
      .failIfNotSuccessful();

    // Create quality gate with conditions on variations
    WsQualityGates.CreateWsResponse simple = tester.qGates().generate();
    tester.qGates().service()
      .createCondition(CreateConditionRequest.builder().setQualityGateId(simple.getId()).setMetricKey("ncloc").setPeriod(1).setOperator("EQ").setWarning("0").build());
    Project project = tester.projects().generate(null);
    tester.qGates().associateProject(simple, project);

    SonarScanner analysis = SonarScanner.create(projectDir("qualitygate/xoo-sample")).setProperty("sonar.projectKey", project.getKey());
    orchestrator.executeBuild(analysis);
    assertThat(getGateStatusMeasure(project).getValue()).isEqualTo("OK");

    orchestrator.executeBuild(analysis);
    assertThat(getGateStatusMeasure(project).getValue()).isEqualTo("WARN");

    waitUntilAllNotificationsAreDelivered(SMTP_SERVER);

    Iterator<WiserMessage> emails = SMTP_SERVER.getMessages().iterator();

    MimeMessage message = emails.next().getMimeMessage();
    assertThat(message.getHeader("To", null)).isEqualTo("<test@example.org>");
    assertThat((String) message.getContent()).contains("This is a test message from SonarQube");

    assertThat(emails.hasNext()).isTrue();
    message = emails.next().getMimeMessage();
    assertThat(message.getHeader("To", null)).isEqualTo("<tester@example.org>");
    assertThat((String) message.getContent())
      .contains("Project: Sample")
      .contains("Version: 1.0-SNAPSHOT")
      .contains("Quality gate status: Orange (was Green)")
      .contains("Quality gate threshold: Lines of Code variation = 0 since previous version")
      .contains("/dashboard?id=" + project.getKey());
    assertThat(emails.hasNext()).isFalse();
  }

  private Measure getGateStatusMeasure(Project project) {
    return getMeasure(orchestrator, project.getKey(), "alert_status");
  }

  private static void waitUntilAllNotificationsAreDelivered(Wiser smtpServer) throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      if (smtpServer.getMessages().size() == 2) {
        break;
      }
      Thread.sleep(1_000);
    }
  }
}
