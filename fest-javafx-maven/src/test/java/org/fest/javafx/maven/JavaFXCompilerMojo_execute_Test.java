/*
 * Created on Jan 24, 2010
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * Copyright @2010 the original author or authors.
 */
package org.fest.javafx.maven;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.fest.util.Files.temporaryFolder;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tools.ant.taskdefs.Javac;
import org.fest.mocks.EasyMockTemplate;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link JavaFXCompilerMojo#execute()}</code>.
 *
 * @author Alex Ruiz
 */
public class JavaFXCompilerMojo_execute_Test {

  private JavaFXHome javaFXHome;
  private JavaFXCompilerFactory compilerFactory;
  private JavaFXCompilerSetup compilerSetup;
  private AntTaskExecutor compilerExecutor;
  private JavaFXCompilerMojo mojo;

  @Before
  public void setUp() {
    javaFXHome = createMock(JavaFXHome.class);
    compilerFactory = createMock(JavaFXCompilerFactory.class);
    compilerSetup = createMock(JavaFXCompilerSetup.class);
    compilerExecutor = createMock(AntTaskExecutor.class);
    mojo = new JavaFXCompilerMojo();
    configureMojo();
  }

  private void configureMojo() {
    mojo.javaFXHome = javaFXHome;
    mojo.compilerFactory = compilerFactory;
    mojo.compilerSetup = compilerSetup;
    mojo.compilerExecutor = compilerExecutor;
    File temporaryFolder = temporaryFolder();
    mojo.sourceDirectory = temporaryFolder;
    mojo.outputDirectory = temporaryFolder;
    mojo.setLog(new LogStub());
  }

  @Test
  public void should_create_and_execute_JavaFX_compiler_Ant_task() {
    final String verifiedJavaFXHome = "c:\\javafx";
    final File javaFXHomeDir = new File("mock");
    final Javac javafxc = new Javac();
    new EasyMockTemplate(javaFXHome, compilerFactory, compilerSetup, compilerExecutor) {
      protected void expectations() throws MojoExecutionException {
        expect(javaFXHome.verify(mojo.JavaFXHome)).andReturn(verifiedJavaFXHome);
        expect(javaFXHome.reference(verifiedJavaFXHome)).andReturn(javaFXHomeDir);
        expect(compilerFactory.createCompiler(javaFXHomeDir)).andReturn(javafxc);
        compilerSetup.configure(javafxc, mojo, javaFXHomeDir);
        expectLastCall().once();
        compilerExecutor.execute(javafxc);
        expectLastCall().once();
      }

      protected void codeToTest() throws MojoExecutionException {
        mojo.execute();
      }
    }.run();
  }
}