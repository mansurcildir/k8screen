import type { DataResult } from '$lib/model/result/DataResult';
import type { StatefulSet } from '$lib/model/StatefulSet';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const statefulSetAPI = {
  getAllStatefulSets: async (namespace: string): Promise<DataResult<StatefulSet[]>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/stateful-sets`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getStatefulSetDetails: async (namespace: string, name: string): Promise<DataResult<string>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/stateful-sets/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateStatefulSet: async (namespace: string, name: string, body: any): Promise<DataResult<any>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/stateful-sets/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
