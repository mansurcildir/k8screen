import type { Deployment } from '$lib/model/Deployment';
import type { DataResult } from '$lib/model/result/DataResult';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const deploymentAPI = {
  getAllDeployments: async (namespace: string): Promise<DataResult<Deployment[]>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/deployments`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getDeploymentDetails: async (namespace: string, name: string): Promise<DataResult<string>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/deployments/${name}/details`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateDeployment: async (namespace: string, name: string, body: any): Promise<DataResult<any>> => {
    const url = `${SPRING_BASE_URL}/v1/namespaces/${namespace}/deployments/${name}`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
