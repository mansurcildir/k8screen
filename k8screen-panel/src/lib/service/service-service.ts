import type { DataResult } from '$lib/model/result/DataResult';
import type { Service } from '$lib/model/Service';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const serviceAPI = {
  getAllServices: async (namespace: string): Promise<DataResult<Service[]>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getServiceDetails: async (namespace: string, name: string): Promise<DataResult<string>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateService: async (namespace: string, name: string, body: any): Promise<DataResult<any>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
