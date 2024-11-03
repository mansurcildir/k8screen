import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest } from "./http-request";

export const namespaceAPI = {
  getAllNamespaces: async (): Promise<string[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces`;
    return await (await applyGetRequest(url)).json();
  }
}
