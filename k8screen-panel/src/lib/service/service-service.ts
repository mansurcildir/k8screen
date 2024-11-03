import type { Service } from "$lib/model/Service";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest } from "./http-request";

export const serviceAPI = {
  getAllServices: async (namespace: string): Promise<Service[]> => {
    const url = `${SPRING_BASE_URL}/api/kubernetes/namespaces/${namespace}/services`;
    return await (await applyGetRequest(url)).json();
  }
}
