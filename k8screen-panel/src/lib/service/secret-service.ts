import type { DataResult } from '$lib/model/result/DataResult';
import type { Secret } from '$lib/model/Secret';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const secretAPI = {
  getAllSecrets: async (namespace: string): Promise<DataResult<Secret[]>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/secrets`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getSecretDetails: async (namespace: string, name: string): Promise<DataResult<string>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/secrets/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateSecret: async (namespace: string, name: string, body: any): Promise<DataResult<any>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/secrets/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
