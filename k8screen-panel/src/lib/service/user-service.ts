import type { UserConfig } from '$lib/model/user/UserConfig';
import type { UserItem } from '$lib/model/user/UserItem';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const userAPI = {
  getProfile: async (): Promise<UserItem> => {
    const url = `${SPRING_BASE_URL}/api/v1/users/profile`;
    return await (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateConfig: async (body: UserConfig): Promise<boolean> => {
    const url = `${SPRING_BASE_URL}/api/v1/users/config`;
    return await (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
