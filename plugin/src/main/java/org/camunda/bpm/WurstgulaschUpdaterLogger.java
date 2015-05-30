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

import org.xbill.DNS.Message;
import org.xbill.DNS.Name;

import java.io.IOException;
import java.net.UnknownHostException;

public class WurstgulaschUpdaterLogger extends WurstgulaschLogger {

  public WurstgulaschException unableToGetLocalIpAddress(Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0001", "Unable to get local ip address"), cause);
  }

  public WurstgulaschException unableToParseDomainName(String domainName, Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0002", "Unable to parse domain name '{}'", domainName), cause);
  }

  public WurstgulaschException unableToCreateResolver(String address, Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0003", "Unable to find address for resolver '{}'", address), cause);
  }

  public WurstgulaschException unableToParseRecord(Name name, int type, long ttl, Object record, Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0004", "Unable to parse record '{} {} {} {}'", name, type, ttl, record), cause);
  }

  public WurstgulaschException unableToSendMessage(Message message, Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0005", "Unable to send update message '{}'", message), cause);
  }

  public WurstgulaschException unableToGetLocalHostname(Throwable cause) {
    return new WurstgulaschException(exceptionMessage("0006", "Unable to get local hostname"), cause);
  }

}
