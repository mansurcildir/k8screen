import { SPRING_BASE_URL } from '$lib/utils';
import { applyGetRequestWithBearerHeader } from './http-request';

export const stripeAPI = {
  getPanelSession: async (): Promise<string> => {
    const url = `${SPRING_BASE_URL}/v1/stripe/panel`;
    return (await applyGetRequestWithBearerHeader(url)).text();
  }
};
