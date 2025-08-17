import type { Account } from '$lib/model/account/Account';
import type { DataResult } from '$lib/model/result/DataResult';
import { SPRING_BASE_URL } from '$lib/utils/utils';
import { applyDeleteRequestWithBearerHeader, applyGetRequestWithBearerHeader } from './http-request';

export const accountAPI = {
  getGoogleAccounts: async (): Promise<DataResult<Account[]>> => {
    const url = `${SPRING_BASE_URL}/v1/accounts/google`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

  getGithubAccounts: async (): Promise<DataResult<Account[]>> => {
    const url = `${SPRING_BASE_URL}/v1/accounts/github`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  },

   deleteAccountByUuid: async (accountUuid: string): Promise<void> => {
    const url = `${SPRING_BASE_URL}/v1/accounts/${accountUuid}`;
    await applyDeleteRequestWithBearerHeader(url);
  }
};
