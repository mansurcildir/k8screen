import type { UserConfig } from '$lib/model/user/UserConfig';
import type { UserInfo } from '$lib/model/user/UserInfo';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';

export const userAPI = {
  getProfile: async (): Promise<UserInfo> => {
    const url = `${SPRING_BASE_URL}/api/v1/users/profile`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateConfig: async (body: UserConfig): Promise<Response> => {
    const url = `${SPRING_BASE_URL}/api/v1/users/config`;
    return await applyPutRequestWithBearerHeader(url, JSON.stringify(body));
  }
};
