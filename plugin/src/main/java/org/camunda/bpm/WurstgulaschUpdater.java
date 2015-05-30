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

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.xbill.DNS.*;
import org.xbill.DNS.utils.base64;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WurstgulaschUpdater {

  private static WurstgulaschUpdater instance = new WurstgulaschUpdater();

  protected static WurstgulaschUpdaterLogger LOG = WurstgulaschLogger.UPDATER_LOGGER;

  protected Resolver resolver;
  protected Name zone;
  protected String secret;
  protected String ipAddress;
  protected Name name;
  protected long ttl;

  private WurstgulaschUpdater() { }

  public static WurstgulaschUpdater getInstance() {
    return instance;
  }

  public Resolver getResolver() {
    return resolver;
  }

  public Name getZone() {
    return zone;
  }

  public String getSecret() {
    return secret;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public Name getName() {
    return name;
  }

  public long getTtl() {
    return ttl;
  }

  protected void setResolver(Resolver resolver) {
    this.resolver = resolver;
  }

  protected void setZone(String zone) {
    try {
      this.zone = Name.fromString(zone);
    } catch (TextParseException e) {
      throw LOG.unableToParseDomainName(zone, e);
    }
  }

  protected void setSecret(String secret) {
    this.secret = secret;
  }

  protected void setIpAddress(String ipAddress) {
    if (ipAddress == null) {
      try {
        this.ipAddress = InetAddress.getLocalHost().getHostAddress();
      } catch (UnknownHostException e) {
        throw LOG.unableToGetLocalIpAddress(e);
      }
    }
    else {
      this.ipAddress = ipAddress;
    }
  }

  protected void setName(String name) {
    if (name == null) {
      name = getHostname();
    }

    try {
      this.name = Name.fromString(name, getZone());
    } catch (TextParseException e) {
      throw LOG.unableToParseDomainName(name, e);
    }

    registerName();
  }

  protected void setTtl(long ttl) {
    this.ttl = ttl;
  }

  protected String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw LOG.unableToGetLocalHostname(e);
    }
  }

  protected void setEngineDomainName(String name) {

  }

  public void configure(String resolverAddress, String zone, String secret, String ipAddress, String name, long ttl) {
    setZone(zone);
    setSecret(secret);
    setIpAddress(ipAddress);
    Resolver resolver = createResolver(resolverAddress);
    setResolver(resolver);
    setName(name);
    setTtl(ttl);
  }

  protected Resolver createResolver(String resolverAddress) {
    Resolver resolver = null;
    try {
      resolver = new SimpleResolver(resolverAddress);
    } catch (UnknownHostException e) {
      throw LOG.unableToCreateResolver(resolverAddress, e);
    }
    resolver.setTSIGKey(new TSIG(getZone(), base64.fromString(getSecret())));
    return resolver;
  }

  public WurstgulaschUpdateBuilder update() {
    return new WurstgulaschUpdateBuilder(resolver, zone);
  }

  protected void registerName() {
    update()
      .add(getZone(), Type.A, 300, getIpAddress())
      .replace(getName(), Type.A, 300, getIpAddress())
      .send();
  }

  public void addTask(DelegateTask delegateTask) {
    String name = getTaskDomainName(delegateTask);
    replaceDomainForIp(name);
  }

  public void deleteTask(DelegateTask delegateTask) {
    String name = getTaskDomainName(delegateTask);
    deleteDomainForIp(name);
  }

  public void addProcessDefinition(ProcessDefinition processDefinition) {
    String name = getProcessDefinitionDomainName(processDefinition);
    addDomainForIp(name);
  }

  public void addProcessInstance(DelegateExecution delegateExecution) {
    String name = getProcessInstanceDomainName(delegateExecution);
    replaceDomainForIp(name);
  }

  public void deleteProcessInstance(DelegateExecution delegateExecution) {
    String name = getProcessInstanceDomainName(delegateExecution);
    deleteDomainForIp(name);
  }

  protected void addDomainForIp(String name) {
    update()
      .add(name, Type.A, ttl, getIpAddress())
      .send();
  }

  protected void replaceDomainForIp(String name) {
    update()
      .replace(name, Type.A, ttl, getIpAddress())
      .send();
  }

  protected void deleteDomainForIp(String name) {
    update()
      .delete(name, Type.A, getIpAddress())
      .send();
  }

  protected String getProcessDefinitionDomainName(ProcessDefinition processDefinition) {
    return processDefinition.getKey() + ".definition";
  }

  protected String getProcessInstanceDomainName(DelegateExecution delegateExecution) {
    return delegateExecution.getId() +  ".process";
  }

  protected String getTaskDomainName(DelegateTask delegateTask) {
    return delegateTask.getId() + ".task";
  }

}
