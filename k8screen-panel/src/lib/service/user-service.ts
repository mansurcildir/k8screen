import type { UserConfig } from '$lib/model/user/UserConfig';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyPutRequestWithBearerHeader } from './http-request';

export const userAPI = {
  updateConfig: async (body: UserConfig): Promise<Response> => {
    const url = `${SPRING_BASE_URL}/v1/users/config`;
    return await applyPutRequestWithBearerHeader(url, JSON.stringify(body));
  }
};
