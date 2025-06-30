import { goto } from '$app/navigation';
import type { GoogleLoginReq } from '$lib/model/user/GoogleLoginReq';
import type { LoginReq } from '$lib/model/user/LoginReq';
import type { LoginRes } from '$lib/model/user/LoginRes';
import type { UserForm } from '$lib/model/user/UserForm';
import type { UserInfo } from '$lib/model/user/UserInfo';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequestWithBearerHeader, applyGetRequestWithRefreshToken, applyPostRequest } from './http-request';
import { clearTokens, getAllTokens, setTokens } from './storage-manager';
import { isTokenExpired } from './token-decoder';

export const authAPI = {
  register: async (body: UserForm): Promise<{ access_token: string; refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/v1/auth/register`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  login: async (body: LoginReq): Promise<LoginRes> => {
    const url = `${SPRING_BASE_URL}/v1/auth/login`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  loginGoogle: async (body: GoogleLoginReq): Promise<LoginRes> => {
    const url = `${SPRING_BASE_URL}/v1/auth/login/google`;
    return (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  logout: async (): Promise<void> => {
    const url = `${SPRING_BASE_URL}/v1/auth/logout`;
    await applyGetRequestWithBearerHeader(url);
    clearTokens();
  },

  getProfile: async (): Promise<UserInfo> => {
    const url = `${SPRING_BASE_URL}/v1/auth/profile`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getAccessToken: async (): Promise<LoginRes> => {
    return (await applyGetRequestWithRefreshToken(`${SPRING_BASE_URL}/v1/auth/refresh`)).json();
  },

  refreshToken: async (): Promise<LoginRes> => {
    const data: LoginRes = await authAPI.getAccessToken();
    localStorage.setItem('access-token', data.access_token);
    return data;
  },

  unAuthorize: () => {
    clearTokens();
    window.location.href = '/login';
    throw new Error('Unauthorized');
  },

  authorize: async (code: string | null = null): Promise<void> => {
    if (code) {
      return await loginGoogle(code);
    }

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

const loginGoogle = async (code: string) => {
  const data = await authAPI.loginGoogle({ code });
  setTokens(data?.access_token, data?.refresh_token);
  goto('/');
};
