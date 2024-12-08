import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest, applyPostRequest } from "./http-request";

export const namespaceAPI = {
  getAllNamespaces: async (): Promise<string[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces`;
    return await (await applyGetRequest(url)).json();
  },

  createNamespace: async (body: string): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces`;
    return await (await applyPostRequest(url, body)).json();
  }
}
