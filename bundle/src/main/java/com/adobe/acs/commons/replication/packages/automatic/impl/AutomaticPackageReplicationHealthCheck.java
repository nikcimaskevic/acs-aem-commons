/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2020 - Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.commons.replication.packages.automatic.impl;

import java.util.List;

import org.apache.sling.hc.api.HealthCheck;
import org.apache.sling.hc.api.Result;
import org.apache.sling.hc.api.Result.Status;
import org.apache.sling.hc.api.ResultLog;
import org.apache.sling.hc.api.ResultLog.Entry;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.adobe.acs.commons.replication.packages.automatic.AutomaticPackageReplicator;

@Component(service = HealthCheck.class, immediate = true, property = {
    HealthCheck.NAME + "=Automatic Package Replication", HealthCheck.TAGS + "=replication",
    HealthCheck.TAGS + "=packages" })
public class AutomaticPackageReplicationHealthCheck implements HealthCheck {

  @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY)
  public List<AutomaticPackageReplicator> replicators;

  @Override
  public Result execute() {
    ResultLog log = new ResultLog();
    replicators.stream().forEach(r -> {
      if (r.getLastStatus() != null) {
        log.add(new Entry(r.getLastStatus(), String.format("Automatic Package Replicator %s has a last status of: %s",
            r.toString(), r.getLastStatus().name())));
      } else {
        log.add(new Entry(Status.INFO, String.format("Automatic Package Replicator %s has not run", r.toString())));
      }
    });
    return new Result(log);
  }

}