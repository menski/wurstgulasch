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

import org.xbill.DNS.*;

import java.io.IOException;

public class WurstgulaschUpdateBuilder {

  protected static WurstgulaschUpdaterLogger LOG = WurstgulaschLogger.UPDATER_LOGGER;

  protected Resolver resolver;
  protected Name zone;
  protected Update update;

  public WurstgulaschUpdateBuilder(Resolver resolver, Name zone) {
    this.resolver = resolver;
    this.zone = zone;
    update = new Update(zone);
  }

  public WurstgulaschUpdateBuilder add(String subDomain, int type, long ttl, String record) {
    Name name = concatName(subDomain);
    return add(name, type, ttl, record);
  }

  public WurstgulaschUpdateBuilder add(String subDomain, int type, long ttl, Name record) {
    Name name = concatName(subDomain);
    return add(name, type, ttl, record);
  }

  public WurstgulaschUpdateBuilder add(Name name, int type, long ttl, Name record) {
    return add(name, type, ttl, record.toString());
  }

  public WurstgulaschUpdateBuilder add(Name name, int type, long ttl, String record) {
    try {
      update.add(name, type, ttl, record);
    }
    catch (IOException e) {
      throw LOG.unableToParseRecord(name, type, ttl, record, e);
    }
    return this;
  }

  public WurstgulaschUpdateBuilder replace(String subDomain, int type, long ttl, String record) {
    Name name = concatName(subDomain);
    return replace(name, type, ttl, record);
  }

  public WurstgulaschUpdateBuilder replace(String subDomain, int type, long ttl, Name record) {
    Name name = concatName(subDomain);
    return replace(name, type, ttl, record);
  }

  public WurstgulaschUpdateBuilder replace(Name name, int type, long ttl, Name record) {
    return replace(name, type, ttl, record.toString());
  }

  public WurstgulaschUpdateBuilder replace(Name name, int type, long ttl, String record) {
    try {
      update.replace(name, type, ttl, record);
    }
    catch (IOException e) {
      throw LOG.unableToParseRecord(name, type, ttl, record, e);
    }
    return this;
  }

  public WurstgulaschUpdateBuilder delete(String subDomain, int type, String record) {
    Name name = concatName(subDomain);
    return delete(name, type, record);
  }

  public WurstgulaschUpdateBuilder delete(Name name, int type, String record) {
    try {
      update.delete(name, type, record);
    } catch (IOException e) {
      throw LOG.unableToParseRecord(name, type, 0, record, e);
    }
    return this;
  }

  public Message send() {
    try {
      return resolver.send(update);
    } catch (IOException e) {
      throw LOG.unableToSendMessage(update, e);
    }
  }

  protected Name concatName(String subDomain) {
    try {
      return Name.fromString(subDomain, zone);
    } catch (TextParseException e) {
      throw LOG.unableToParseDomainName(subDomain, e);
    }
  }

}
