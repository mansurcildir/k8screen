import type { UserItem } from '$lib/model/user/UserItem';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader } from './http-request';

export const userAPI = {
  getProfile: async (): Promise<UserItem> => {
    const url = `${SPRING_BASE_URL}/api/users/profile`;
    return await (await applyGetRequestWithBearerHeader(url)).json();
  }
};
