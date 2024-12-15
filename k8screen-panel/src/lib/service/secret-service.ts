import type { Secret } from "$lib/model/Secret";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from "./http-request";

export const secretAPI = {
  getAllSecrets: async (namespace: string): Promise<Secret[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/secrets`;
    return await (await applyGetRequestWithBearerHeader(url)).json();
  },

  getSecretDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/secrets/${name}/details`;
    return await (await applyGetRequestWithBearerHeader(url)).text();
  },

  updateSecret: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/secrets/${name}`;
    return await (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
}
