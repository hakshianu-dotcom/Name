lab 12
Step 1 – Create Maven Java Project
mvn archetype:generate
GroupId: com.devops.lab
ArtifactId: lab12-maven
Packaging: jar
Archetype: maven-archetype-quickstart
Move inside:
cd lab12-maven
Update pom.xml for JUnit 5
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.devops.lab</groupId>
    <artifactId>lab12-maven</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>lab12-maven</name>
    <url>http://www.example.com</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.release>17</maven.compiler.release>
    </properties>

    <!--  ✅ UPDATED DEPENDENCIES  -->
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!--  ✅ UPDATED BUILD  -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
Step 3 – Create Java Class
src/main/java/com/devops/lab/Calculator.java
package com.devops.lab;
public class Calculator {
    public int multiply(int a, int b) {
        return a * b;
    }
}
src/test/java/com/devops/lab/CalculatorTest.java
package com.devops.lab;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    void testMultiply() {
        assertEquals(20, calculator.multiply(4, 5));
    }
}
Step 5 – Run Tests Locally
mvn clean test
Verify:
target/surefire-reports
Contains XML reports.
Step 6 – Push to Git
git init
git add .
git commit -m "Maven project with JUnit"
git remote add origin <repo-url>
git push -u origin main
7.2 Configure Tools
Manage Jenkins → Global Tool Configuration
Add:
JDK 17
Maven (Maven3)
Step 8 – Create Maven Pipeline Job
New Item → Pipeline → Name: Lab-12-Maven
Select: Pipeline script from SCM Repository: <repo-url> Branch: main
create Jenkinsfile
pipeline {
    agent any

    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                git '<repo-url>'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
Click Build Now.

lab 14
Step 1 – Use Existing JUnit Project
If not available, create simple structure:
src/main/java/com/devops/lab/
src/test/java/com/devops/lab/
pom.xml

package com.devops.lab;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }
}

// Sample test class
class CalculatorTest {

    Calculator calculator = new Calculator();

    @Test
    void testAdd() {
        assertEquals(10, calculator.add(5, 5));
    }

    @Test
    void testMultiply() {
        assertEquals(20, calculator.multiply(4, 5));
    }
}
 jacoco
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>
  <groupId>com.devops.lab</groupId>
  <artifactId>lab12</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>17</maven.compiler.release>
  </properties>

  <dependencies>
    <!-- JUnit Jupiter for testing -->
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter</artifactId>
      <version>5.10.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <!-- Maven Surefire Plugin to run tests -->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.1.2</version>
      </plugin>

      <!-- JaCoCo Plugin for code coverage -->
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>0.8.10</version>
        <executions>
          <!-- Prepare agent before tests -->
          <execution>
            <goals>
              <goal>prepare-agent</goal>
            </goals>
          </execution>
          <!-- Generate coverage report after tests -->
          <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
              <goal>report</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
Step 3 – Run Coverage Locally
Execute:
mvn clean test
Step 6 – Push Code to Git
git add .
git commit -m "Added JaCoCo coverage"
git push
Jenkinsfile
pipeline {
    agent any
    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }
    stages {
        stage('Checkout') {
            steps {
                git '<repo-url>'
            }
        }
        stage('Build & Test with Coverage') {
            steps {
                sh 'mvn clean test'
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/site/jacoco/**', fingerprint: true
        }
    }
}
Step 8 – Run Jenkins Pipeline
Click:
Build Now
Step 10 – Validate Failure Scenario
Remove multiply test.
Run pipeline.
Expected:
Coverage below threshold
Build fails
