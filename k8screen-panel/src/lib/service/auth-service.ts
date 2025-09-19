import type { DataResult } from '$lib/model/result/DataResult';
import type { Result } from '$lib/model/result/Result';
import type { EmailForm } from '$lib/model/user/EmailForm';
import type { LoginReq } from '$lib/model/user/LoginReq';
import type { LoginRes } from '$lib/model/user/LoginRes';
import type { RecoverPasswordForm } from '$lib/model/user/RecoverPasswordForm';
import type { ResetPasswordForm } from '$lib/model/user/ResetPasswordForm';
import type { UserForm } from '$lib/model/user/UserForm';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import {
  applyGetRequestWithBearerHeader,
  applyGetRequestWithRefreshToken,
  applyPostRequest,
  applyPutRequest,
  applyPutRequestWithBearerHeader
} from './http-request';
import { clearTokens, getAllTokens } from './storage-manager';
import { isTokenExpired } from './token-decoder';

export const authAPI = {
  register: async (body: UserForm): Promise<DataResult<LoginRes>> => {
    const url = `${SPRING_BASE_URL}/v1/auth/register`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  login: async (body: LoginReq): Promise<DataResult<LoginRes>> => {
    const url = `${SPRING_BASE_URL}/v1/auth/login`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  logout: async (): Promise<void> => {
    const url = `${SPRING_BASE_URL}/v1/auth/logout`;
    await applyGetRequestWithBearerHeader(url);
    clearTokens();
  },

  sendPasswordRecovery: async (body: EmailForm): Promise<Result> => {
    const url = `${SPRING_BASE_URL}/v1/auth/password-recovery`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  recoverPassword: async (body: RecoverPasswordForm): Promise<Result> => {
    const url = `${SPRING_BASE_URL}/v1/auth/recover-password`;
    return (await applyPutRequest(url, JSON.stringify(body))).json();
  },

  resetPassword: async (body: ResetPasswordForm): Promise<Result> => {
    const url = `${SPRING_BASE_URL}/v1/auth/reset-password`;
    return (await applyPutRequestWithBearerHeader(url, JSON.stringify(body))).json();
  },

  getAccessToken: async (): Promise<DataResult<LoginRes>> => {
    return (await applyGetRequestWithRefreshToken(`${SPRING_BASE_URL}/v1/auth/refresh`)).json();
  },

  refreshToken: async () => {
    const res: DataResult<LoginRes> = await authAPI.getAccessToken();
    localStorage.setItem('access-token', res.data.access_token);
  },

  unAuthorize: () => {
    clearTokens();
    window.location.href = '/login';
    throw new Error('Unauthorized');
  },

  authorize: async (): Promise<void> => {
    const tokens = getAllTokens();

    if (!tokens.accessToken || isTokenExpired(tokens.accessToken)) {
      if (!tokens.refreshToken || isTokenExpired(tokens.refreshToken)) {
        authAPI.unAuthorize();
      }

      try {
        await authAPI.refreshToken();
      } catch {
        authAPI.unAuthorize();
      }
    }
  }
};
