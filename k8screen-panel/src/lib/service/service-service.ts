import type { Service } from '$lib/model/Service';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const serviceAPI = {
  getAllServices: async (namespace: string): Promise<Service[]> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getServiceDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).text();
  },

  updateService: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/services/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
