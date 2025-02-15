import type { ConfigInfo } from '$lib/model/config/ConfigInfo';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyDeleteRequestWithBearerHeader, applyGetRequestWithBearerHeader } from './http-request';
import { getAccessToken } from './storage-manager';

export const configAPI = {
  getAllConfigs: async (): Promise<ConfigInfo[]> => {
    const url = `${SPRING_BASE_URL}/api/v1/configs`;
    return await (await applyGetRequestWithBearerHeader(url)).json();
  },

  uploadConfig: async (formData: FormData) => {
    return await fetch(`${SPRING_BASE_URL}/api/v1/configs/upload`, {
      method: 'POST',
      body: formData,
      headers: {
        Authorization: `Bearer ${getAccessToken()}`
      }
    });
  },

  deleteConfig: async (fileName: string) => {
    const url = `${SPRING_BASE_URL}/api/v1/configs/delete?name=${fileName}`;
    return await (await applyDeleteRequestWithBearerHeader(url)).json();
  }
};
