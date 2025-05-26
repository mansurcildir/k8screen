import type { StatefulSet } from '$lib/model/StatefulSet';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const statefulSetAPI = {
  getAllStatefulSets: async (namespace: string): Promise<StatefulSet[]> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/stateful-sets`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getStatefulSetDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/stateful-sets/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).text();
  },

  updateStatefulSet: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/v1/namespaces/${namespace}/stateful-sets/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
