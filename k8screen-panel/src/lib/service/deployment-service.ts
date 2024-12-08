import type { Deployment } from "$lib/model/Deployment";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest, applyPutRequest } from "./http-request";

export const deploymentAPI = {
  getAllDeployments: async (namespace: string): Promise<Deployment[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/deployments`;
    return await (await applyGetRequest(url)).json();
  },

  getDeploymentDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/deployments/${name}/details`;
    return await (await applyGetRequest(url)).text();
  },

  updateDeployment: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/deployments/${name}`;
    return await (await applyPutRequest(url, JSON.stringify(body))).json();
  }
}
