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

import java.net.UnknownHostException;

public class WurstgulaschListenerLogger extends WurstgulaschLogger {

  public void parseProcessDefinition(String processDefinitionKey) {
    logInfo("0001", "Parsing process definition '{}'", processDefinitionKey);
  }

  public void processInstanceStarted(String processInstanceId) {
    logInfo("0002", "Process instance '{}' started", processInstanceId);
  }

  public void processInstanceEnded(String processInstanceId) {
    logInfo("0003", "Process instance '{}' ended", processInstanceId);
  }

  public void taskStarted(String taskId) {
    logInfo("0004", "Task '{}' started", taskId);
  }

  public void taskEnded(String taskId) {
    logInfo("0005", "Task '{}' ended", taskId);
  }

  public void unableToGetLocalIpAddress(UnknownHostException e) {
    logError("0006", "Unable to get local ip address of host", e);
  }
}
