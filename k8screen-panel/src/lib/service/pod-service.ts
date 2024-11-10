import type { Pod } from "$lib/model/Pod";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { json } from "@sveltejs/kit";
import { applyGetRequest, applyPostRequest, applyPutRequest } from "./http-request";

export const podAPI = {
  getAllPods: async (namespace: string): Promise<Pod[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods`;
    return await (await applyGetRequest(url)).json();
  },

  getPodLogs: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods/${name}/logs`;
    return await (await applyGetRequest(url)).text();
  },

  getPodDetails: async (namespace: string, name: string): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods/${name}/details`;
    return await (await applyGetRequest(url)).text();
  },

  updatePod: async (namespace: string, name: string, body: any): Promise<any> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods/${name}`;
    return await (await applyPutRequest(url, JSON.stringify(body))).json();
  },

  exec: async (namespace: string, name: string, cmd: string[]): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/pods/${name}/exec`;
    return await (await applyPostRequest(url, JSON.stringify(cmd))).text();
  }
}
