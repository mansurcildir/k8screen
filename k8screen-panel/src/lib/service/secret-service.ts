import type { Secret } from "$lib/model/Secret";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest } from "./http-request";

export const secretAPI = {
  getAllSecrets: async (namespace: string): Promise<Secret[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/secrets`;
    return await (await applyGetRequest(url)).json();
  }
}
