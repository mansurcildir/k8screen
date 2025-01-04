import { writable } from 'svelte/store';
import { podAPI } from '$lib/service/pod-service';
import type { Pod } from './model/Pod';
import type { Deployment } from './model/Deployment';
import { deploymentAPI } from './service/deployment-service';
import type { Secret } from './model/Secret';
import { secretAPI } from './service/secret-service';
import type { Service } from './model/Service';
import { serviceAPI } from './service/service-service';
import { statefulSetAPI } from './service/statefulset-service';
import type { StatefulSet } from './model/StatefulSet';

export const loadingDeployment = writable<boolean>();
export const loadingPod = writable<boolean>();
export const loadingSecret = writable<boolean>();
export const loadingService = writable<boolean>();
export const loadingStatefulSet = writable<boolean>();

export const deployments = writable<Deployment[]>([]);
export const pods = writable<Pod[]>([]);
export const secrets = writable<Secret[]>([]);
export const services = writable<Service[]>([]);
export const statefulsets = writable<StatefulSet[]>([]);

export const getAllDeployments = async (namespace: string) => {
  loadingDeployment.set(true);
  const data = await deploymentAPI.getAllDeployments(namespace);
  deployments.set(data);
  loadingDeployment.set(false);
};

export const getAllPods = async (namespace: string) => {
  loadingPod.set(true);
  const data = await podAPI.getAllPods(namespace);
  pods.set(data);
  loadingPod.set(false);
};

export const getAllSecrets = async (namespace: string) => {
  loadingSecret.set(true);
  const data = await secretAPI.getAllSecrets(namespace);
  secrets.set(data);
  loadingSecret.set(false);
};

export const getAllServices = async (namespace: string) => {
  loadingService.set(true);
  const data = await serviceAPI.getAllServices(namespace);
  services.set(data);
  loadingService.set(false);
};

export const getAllStatefulSets = async (namespace: string) => {
  loadingStatefulSet.set(true);
  const data = await statefulSetAPI.getAllStatefulSets(namespace);
  statefulsets.set(data);
  loadingStatefulSet.set(false);
};

export const refresh = async (namespace: string) => {
  getAllDeployments(namespace);
  getAllPods(namespace);
  getAllSecrets(namespace);
  getAllServices(namespace);
  getAllStatefulSets(namespace);
};
