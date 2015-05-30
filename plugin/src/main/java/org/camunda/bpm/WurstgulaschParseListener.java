/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.core.model.CoreModelElement;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;

public class WurstgulaschParseListener extends AbstractBpmnParseListener {

  protected static WurstgulaschListenerLogger LOG = WurstgulaschLogger.LISTENER_LOGGER;
  protected static WurstgulaschUpdater UPDATER = WurstgulaschUpdater.getInstance();

  public final static ExecutionListener EXECUTION_LISTENER = new WurstgulaschExecutionListener();
  public final static TaskListener TASK_LISTENER = new WurstgulaschTaskListener();

  protected void addStartEventListener(CoreModelElement activity) {
    activity.addListener(ExecutionListener.EVENTNAME_START, EXECUTION_LISTENER);
  }

  protected void addEndEventListener(CoreModelElement activity) {
    activity.addListener(ExecutionListener.EVENTNAME_END, EXECUTION_LISTENER);
  }

  protected void addTaskCreateListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_CREATE, TASK_LISTENER);
  }

  protected void addTaskCompleteListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_COMPLETE, TASK_LISTENER);
  }

  protected void addTaskDeleteListeners(TaskDefinition taskDefinition) {
    taskDefinition.addTaskListener(TaskListener.EVENTNAME_DELETE, TASK_LISTENER);
  }

  @Override
  public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
    LOG.parseProcessDefinition(processDefinition.getKey());
    UPDATER.addProcessDefinition(processDefinition);
    addStartEventListener(processDefinition);
    addEndEventListener(processDefinition);
  }

  @Override
  public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
    UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
    TaskDefinition taskDefinition = activityBehavior.getTaskDefinition();
    addTaskCreateListeners(taskDefinition);
    addTaskCompleteListeners(taskDefinition);
    addTaskDeleteListeners(taskDefinition);
  }

}
