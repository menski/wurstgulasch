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

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

public class WurstgulaschTaskListener implements TaskListener {

  protected static WurstgulaschListenerLogger LOG = WurstgulaschLogger.LISTENER_LOGGER;
  protected static WurstgulaschUpdater UPDATER = WurstgulaschUpdater.getInstance();

  @Override
  public void notify(DelegateTask delegateTask) {
    String eventName = delegateTask.getEventName();

    if (TaskListener.EVENTNAME_CREATE.equals(eventName)) {
      notifyTaskCreated(delegateTask);
    }
    if (TaskListener.EVENTNAME_COMPLETE.equals(eventName)) {
      notifyTaskEnded(delegateTask);
    }
    if (TaskListener.EVENTNAME_DELETE.equals(eventName)) {
      notifyTaskEnded(delegateTask);
    }
  }

  protected void notifyTaskCreated(DelegateTask delegateTask) {
    LOG.taskStarted(delegateTask.getId());
    UPDATER.addTask(delegateTask);
  }

  protected void notifyTaskEnded(DelegateTask delegateTask) {
    LOG.taskEnded(delegateTask.getId());
    UPDATER.deleteTask(delegateTask);
  }

}
