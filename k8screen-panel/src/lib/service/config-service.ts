import type { ConfigInfo } from '$lib/model/config/ConfigInfo';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { authAPI } from './auth-service';
import { applyDeleteRequestWithBearerHeader, applyGetRequestWithBearerHeader } from './http-request';
import { getAccessToken } from './storage-manager';

export const configAPI = {
  getAllConfigs: async (): Promise<ConfigInfo[]> => {
    const url = `${SPRING_BASE_URL}/v1/configs`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  uploadConfig: async (formData: FormData) => {
    await authAPI.authorize();

    const response = await fetch(`${SPRING_BASE_URL}/v1/configs/upload`, {
      method: 'POST',
      body: formData,
      headers: {
        Authorization: `Bearer ${getAccessToken()}`
      }
    });

    if (!response.ok) {
      throw await response.json();
    }

    return response;
  },

  deleteConfig: async (fileName: string) => {
    const url = `${SPRING_BASE_URL}/v1/configs/delete?name=${fileName}`;
    return await applyDeleteRequestWithBearerHeader(url);
  }
};
