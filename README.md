### License

Copyright 2008-2017 SonarSource.

Licensed under the [GNU Lesser General Public License, Version 3.0](http://www.gnu.org/licenses/lgpl.txt)

### Build status

[![Build Status](https://travis-ci.org/SonarSource/sonarqube.svg?branch=master)](https://travis-ci.org/SonarSource/sonarqube)

### Links

Project website: http://www.sonarqube.org/

Documentation: http://docs.sonarqube.org/display/SONAR

Issue tracking: http://jira.sonarsource.com/browse/SONAR

### 修改
1. 服务器端: 版本是sonarqube-6.7.7 TLS版本；
2. 添加sonar-scanner-client模块，功能是扫描客户端，版本是sonar-scanner-cli-3.3.0.1492 版本；采用这个版本的原因是

\<parent\>

\<groupId\>org.sonarsource.parent\</groupId\>

\<artifactId\>parent\</artifactId\>

\<version\>49\</version\>

\</parent\>

依赖的版本是49。这个跟sonarqube-6.7.7 TLS版本的依赖是一致的。