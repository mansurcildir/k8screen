import type { DataResult } from '$lib/model/result/DataResult';
import type { UserConfig } from '$lib/model/user/UserConfig';
import type { UserInfo } from '$lib/model/user/UserInfo';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import type { Result } from 'postcss';
import { applyGetRequestWithBearerHeader, applyPutRequestWithBearerHeader } from './http-request';
import type { ProfileForm } from '$lib/model/user/ProfileForm';

export const userAPI = {
  updateConfig: async (body: UserConfig): Promise<Response> => {
    const url = `${SPRING_BASE_URL}/v1/users/config`;
    return await applyPutRequestWithBearerHeader(url, JSON.stringify(body));
  },

  getProfile: async (): Promise<DataResult<UserInfo>> => {
    const url = `${SPRING_BASE_URL}/v1/users/profile`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  updateProfile: async (body: ProfileForm): Promise<Result> => {
    const url = `${SPRING_BASE_URL}/v1/users/profile`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  }
};
