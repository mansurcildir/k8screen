import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPostRequestWithBearerHeader } from './http-request';

export const namespaceAPI = {
  getAllNamespaces: async (): Promise<string[]> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  createNamespace: async (body: string): Promise<any> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces`;
    return (await applyPostRequestWithBearerHeader(url, body)).json();
  }
};
