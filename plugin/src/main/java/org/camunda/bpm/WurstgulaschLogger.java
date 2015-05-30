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

import org.camunda.commons.logging.BaseLogger;

public abstract class WurstgulaschLogger extends BaseLogger {

  public static final String PROJECT_CODE = "WURST";
  public static final String PROJECT_LOGGER = "org.camunda.bpm.wurstgulasch";

  public static WurstgulaschListenerLogger LISTENER_LOGGER = createLogger(WurstgulaschListenerLogger.class, PROJECT_CODE, PROJECT_LOGGER, "01");
  public static WurstgulaschUpdaterLogger UPDATER_LOGGER = createLogger(WurstgulaschUpdaterLogger.class, PROJECT_CODE, PROJECT_LOGGER, "02");

}
