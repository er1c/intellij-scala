/*
 * Copyright 2000-2006 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.scala.lang.actions;

import org.jetbrains.plugins.scala.testcases.BaseScalaFileSetTestCase;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.idea.IdeaTestApplication;
import com.intellij.codeInsight.editorActions.EnterHandler;
import com.intellij.psi.PsiFile;

import java.io.IOException;

/**
 * @author Ilya.Sergey
 */
public abstract class ActionTest extends BaseScalaFileSetTestCase {

  protected static final String CARET_MARKER = "<caret>";

  public ActionTest(String path) {
    super(path);
  }

  /**
   * Runs editor action
   *
   * @param runnable
   */
  public static void runAsWriteAction(final Runnable runnable) {
    ApplicationManager.getApplication().runWriteAction(runnable);
  }

  /**
   * Returns context for action performing
   *
   * @return
   * @throws com.intellij.openapi.util.InvalidDataException
   *
   * @throws java.io.IOException
   */
  protected myDataContext getDataContext(PsiFile file) throws InvalidDataException, IOException {
    final myDataContext dataContext = new myDataContext(file);
    IdeaTestApplication.getInstance().setDataProvider(dataContext);
    return dataContext;
  }

  /**
   * Removes CARET_MARKER from file text
   * @param text
   * @return
   */
  protected String removeMarker(String text) {
    int index = text.indexOf(CARET_MARKER);
    return text.substring(0, index) + text.substring(index + CARET_MARKER.length());
  }


  /**
   * Performs cpecified action
   *
   * @param project
   * @param action
   */
  public static void performAction(final Project project, final Runnable action) {
    runAsWriteAction(new Runnable() {
      public void run() {
        CommandProcessor.getInstance().executeCommand(project, action, "Execution", null);
      }
    });
  }

  /**
   * Implements current DataContext logic
   */
  public class myDataContext implements DataContext, DataProvider {

    PsiFile myFile;

    public myDataContext(PsiFile file) {
      myFile = file;
    }
    @Nullable
    public Object getData(@NonNls String dataId) {
      if (DataConstants.LANGUAGE.equals(dataId)) return myFile.getLanguage();
      if (DataConstants.PROJECT.equals(dataId)) return myFile.getProject();
      return null;
    }
  }

}
