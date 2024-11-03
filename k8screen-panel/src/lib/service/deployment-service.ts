import type { Deployment } from "$lib/model/Deployment";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest } from "./http-request";

export const deploymentAPI = {
  getAllPods: async (namespace: string): Promise<Deployment[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/deployments`;
    return await (await applyGetRequest(url)).json();
  }
}
