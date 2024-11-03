import type { Pod } from "$lib/model/Pod";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest } from "./http-request";

export const podAPI = {
  getAllPods: async (namespace: string): Promise<Pod[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods`;
    return await (await applyGetRequest(url)).json();
  },

  getPodLogs: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods/${name}/logs`;
    return await (await applyGetRequest(url)).text();
  }
}
