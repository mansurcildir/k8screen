import type { LoginReq } from "$lib/model/user/LoginReq";
import type { UserForm } from "$lib/model/user/UserForm";
import { SPRING_BASE_URL } from "$lib/utils/utils";
import { applyGetRequest, applyGetRequestOptional, applyPostRequest } from "./http-request";
import { checkTokensExist, clearTokens, getAllTokens } from "./storage-manager";
import { isTokenExpired } from "./token-decoder";

export const authAPI = {
  register: async (body: UserForm): Promise<{ access_token: string, refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/auth/register`;
    console.log(body)
    return await (await applyPostRequest(url, JSON.stringify(body))).json();
  },

  login: async (body: LoginReq): Promise<{ access_token: string, refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/auth/login`;
    return await (await applyPostRequest(url, JSON.stringify(body))).json();
  },


  loginGoogle: async (): Promise<{ access_token: string, refresh_token: string }> => {
    const url = `${SPRING_BASE_URL}/api/auth/login/google`;
    return await (await applyGetRequest(url)).json();
  },
  
  logout: async (): Promise<string> => {
    const url = `${SPRING_BASE_URL}/api/auth/logout`;
    return await (await applyGetRequest(url)).text();
  },

  getAccessToken: async (body: string): Promise<{ access_token: string }> => {
    const headers = () => ({
      'Content-Type': 'application/json',
      'Refresh-Token': body
    });

    return await (await applyGetRequestOptional(`${SPRING_BASE_URL}/api/auth/access-token`, headers)).json();
  },

  refreshToken: async (refreshToken: string) => {
    return authAPI.getAccessToken(refreshToken)
    .then((data: { access_token: string }) => {
      localStorage.setItem("access-token", data.access_token);
      return data;
    })
  },

  authenticate: async (): Promise<boolean> => {
    const tokens = getAllTokens();

    if (!tokens.accessToken || !tokens.refreshToken) {
      console.error('Access Token or Refresh Token is not provided');
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

}

const unAuthorize = () => {
  clearTokens();
  window.location.href = "/login";
  return false;
}
