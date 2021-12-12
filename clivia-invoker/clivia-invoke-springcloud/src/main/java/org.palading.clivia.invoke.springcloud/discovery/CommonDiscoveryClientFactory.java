/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.invoke.springcloud.discovery;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * @author palading_cr
 * @title CommonDiscoveryClientFactory
 * @project clivia
 */
@Deprecated
public class CommonDiscoveryClientFactory implements DiscoveryClient {

    private static Logger logger = LoggerFactory.getLogger(CommonDiscoveryClientFactory.class);

    private DiscoveryClient discoveryClient;

    public CommonDiscoveryClientFactory(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> instances = new ArrayList<>();
        logger.info("Fetching instances for app: " + serviceId);
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            logger.warn("DiscoveryClient returned null or empty for service: " + serviceId);
            return instances;
        }
        // try {
        // log.info("Received instance list for service: " + serviceId + ", size=" + serviceInstances.size());
        // for (ServiceInstance serviceInstance : serviceInstances) {
        // Instance instance = marshall(serviceInstance);
        // if (instance != null) {
        // instances.add(instance);
        // }
        // }
        // } catch (Exception e) {
        // log.warn("Failed to retrieve instances from DiscoveryClient", e);
        // }
        return instances;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public List<String> getServices() {
        return null;
    }

    // /**
    // * Private helper that marshals the information from each instance into something that Turbine can understand.
    // * Override this method for your own implementation.
    // *
    // * @param serviceInstance
    // * @return Instance
    // */
    // Instance marshall(ServiceInstance serviceInstance) {
    // String hostname = serviceInstance.getHost();
    // String managementPort = serviceInstance.getMetadata().get("management.port");
    // String port = managementPort == null ? String.valueOf(serviceInstance.getPort()) : managementPort;
    // String cluster = getClusterName(serviceInstance);
    // Boolean status = Boolean.TRUE; // TODO: where to get?
    // if (hostname != null && cluster != null && status != null) {
    // Instance instance = getInstance(hostname, port, cluster, status);
    //
    // Map<String, String> metadata = serviceInstance.getMetadata();
    // boolean securePortEnabled = serviceInstance.isSecure();
    //
    // addMetadata(instance, hostname, port, securePortEnabled, port, metadata);
    //
    // return instance;
    // } else {
    // return null;
    // }
    // }
    //
    // protected void addMetadata(Instance instance, String hostname, String port, boolean securePortEnabled, String
    // securePort,
    // Map<String, String> metadata) {
    // // add metadata
    // if (metadata != null) {
    // instance.getAttributes().putAll(metadata);
    // }
    //
    // // add ports
    // instance.getAttributes().put(PORT_KEY, port);
    // if (securePortEnabled) {
    // instance.getAttributes().put(SECURE_PORT_KEY, securePort);
    // }
    // if (this.isCombineHostPort()) {
    // String fusedHostPort = securePortEnabled ? hostname + ":" + securePort : instance.getHostname();
    // instance.getAttributes().put(FUSED_HOST_PORT_KEY, fusedHostPort);
    // }
    // }
    //
    // protected Instance getInstance(String hostname, String port, String cluster, Boolean status) {
    // String hostPart = this.isCombineHostPort() ? hostname + ":" + port : hostname;
    // return new Instance(hostPart, cluster, status);
    // }
    //
    // /**
    // * Helper that fetches the cluster name. Cluster is a Turbine concept and not a commons concept. By default we
    // * choose the amazon serviceId as the cluster. A custom implementation can be plugged in by overriding this
    // method.
    // */
    // protected String getClusterName(Object object) {
    // StandardEvaluationContext context = new StandardEvaluationContext(object);
    // Object value = this.clusterNameExpression.getValue(context);
    // if (value != null) {
    // return value.toString();
    // }
    // return null;
    // }
}
