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
import { toastService } from './service/toast-service';

export const loadingDeployment = writable<boolean>();
export const loadingPod = writable<boolean>();
export const loadingSecret = writable<boolean>();
export const loadingService = writable<boolean>();
export const loadingStatefulSet = writable<boolean>();

export const deployments = writable<Deployment[]>([]);
export const pods = writable<Pod[]>([]);
export const secrets = writable<Secret[]>([]);
export const services = writable<Service[]>([]);
export const statefulSets = writable<StatefulSet[]>([]);

export const getAllDeployments = (namespace: string): Promise<void> => {
  loadingDeployment.set(true);
  return deploymentAPI
    .getAllDeployments(namespace)
    .then((res) => deployments.set(res.data))
    .catch((err) => toastService.show(err.message, 'error'))
    .finally(() => loadingDeployment.set(false));
};

export const getAllPods = (namespace: string): Promise<void> => {
  loadingPod.set(true);
  return podAPI
    .getAllPods(namespace)
    .then((res) => pods.set(res.data))
    .catch((err) => toastService.show(err.message, 'error'))
    .finally(() => loadingPod.set(false));
};

export const getAllSecrets = (namespace: string): Promise<void> => {
  loadingSecret.set(true);
  return secretAPI
    .getAllSecrets(namespace)
    .then((res) => secrets.set(res.data))
    .catch((err) => toastService.show(err.message, 'error'))
    .finally(() => loadingSecret.set(false));
};

export const getAllServices = (namespace: string): Promise<void> => {
  loadingService.set(true);
  return serviceAPI
    .getAllServices(namespace)
    .then((res) => services.set(res.data))
    .catch((err) => toastService.show(err.message, 'error'))
    .finally(() => loadingService.set(false));
};

export const getAllStatefulSets = (namespace: string): Promise<void> => {
  loadingStatefulSet.set(true);
  return statefulSetAPI
    .getAllStatefulSets(namespace)
    .then((res) => statefulSets.set(res.data))
    .catch((err) => toastService.show(err.message, 'error'))
    .finally(() => loadingStatefulSet.set(false));
};

export const refresh = async (namespace: string) => {
  getAllDeployments(namespace);
  getAllPods(namespace);
  getAllSecrets(namespace);
  getAllServices(namespace);
  getAllStatefulSets(namespace);
};
