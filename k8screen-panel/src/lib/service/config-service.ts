import type { ConfigItem } from '$lib/model/config/ConfigItem';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader } from './http-request';

export const configAPI = {
  getAllConfigs: async (): Promise<ConfigItem[]> => {
    const url = `${SPRING_BASE_URL}/api/v1/configs`;
    return await (await applyGetRequestWithBearerHeader(url)).json();
  }
};
