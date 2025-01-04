import type { LoginReq } from '$lib/model/user/LoginReq';
import type { UserForm } from '$lib/model/user/UserForm';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyGetRequest, applyGetRequestOptional, applyPostRequest } from './http-request';
import { checkTokensExist, clearTokens, getAllTokens, setTokens } from './storage-manager';
import { isTokenExpired } from './token-decoder';

export const authAPI = {
  register: async (body: UserForm): Promise<{ access_token: string; refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/v1/auth/register`;
    return await (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  login: async (body: LoginReq): Promise<{ access_token: string; refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/v1/auth/login`;
    return await (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  loginGoogle: async (body: { code: string }): Promise<{ access_token: string; refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/v1/auth/login/google`;
    return await (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  logout: async (): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/v1/auth/logout`;
    return await (await applyGetRequest(url)).text();
  },

  getAccessToken: async (body: string): Promise<{ access_token: string }> => {
    const headers = () => ({
      'Content-Type': 'application/json',
      'Refresh-Token': body
    });

    return await (await applyGetRequestOptional(`${SPRING_BASE_URL}/api/v1/auth/access-token`, headers)).json();
  },

  refreshToken: async (refreshToken: string) => {
    return authAPI.getAccessToken(refreshToken).then((data: { access_token: string }) => {
      localStorage.setItem('access-token', data.access_token);
      return data;
    });
  },

  authorize: async (): Promise<boolean> => {
    const tokens = getAllTokens();
    if (tokens.accessToken && tokens.refreshToken && isTokenExpired(tokens.accessToken)) {
      if (!isTokenExpired(tokens.refreshToken)) {
        await authAPI.refreshToken(tokens.refreshToken);
      } else {
        unAuthorize();
      }
    }

    return true;
  },

  authenticate: async (code: string | null): Promise<boolean> => {
    const tokens = getAllTokens();

    if (!tokens.accessToken || !tokens.refreshToken) {
      if (code) {
        authAPI.loginGoogle({ code: code }).then((data) => {
          setTokens(data?.access_token, data?.refresh_token);
          window.location.href = '/';
        });
        return true;
      }

      return unAuthorize();
    }

    if (isTokenExpired(tokens.accessToken)) {
      if (isTokenExpired(tokens.refreshToken)) {
        console.error('Refresh Token is expired');
        return unAuthorize();
      }

      await authAPI.refreshToken(tokens.refreshToken);

      if (!checkTokensExist()) {
        console.error('Failed to refresh the token');
        return unAuthorize();
      }
    }

    return true;
  }
};

const unAuthorize = () => {
  clearTokens();
  window.location.href = '/login';
  return false;
};
