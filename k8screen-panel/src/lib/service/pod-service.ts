import type { Pod } from '$lib/model/Pod';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import {
  applyGetRequestWithBearerHeader,
  applyPostRequestWithBearerHeader,
  applyPutRequestWithBearerHeader
} from './http-request';

export const podAPI = {
  getAllPods: async (namespace: string): Promise<Pod[]> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/pods`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getPodLogs: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/pods/${name}/logs`;
    return (await applyGetRequestWithBearerHeader(url)).text();
  },

  getPodDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/pods/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).text();
  },

  updatePod: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/pods/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  },

  exec: async (namespace: string, name: string, cmd: string[]): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/pods/${name}/exec`;
    return (await applyPostRequestWithBearerHeader(url, JSON.stringify(cmd))).text();
  }
};
