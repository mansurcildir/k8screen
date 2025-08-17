import type { DataResult } from '$lib/model/result/DataResult';
import { SPRING_BASE_URL } from '$lib/utils';
import { applyGetRequestWithBearerHeader } from './http-request';

export const stripeAPI = {
  getPanelSession: async (): Promise<DataResult<string>> => {
    const url = `${SPRING_BASE_URL}/v1/stripe/panel`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  }
};
