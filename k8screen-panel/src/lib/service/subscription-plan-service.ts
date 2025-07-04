import type { SubscriptionPlanInfo } from '$lib/model/subscription/SubscriptionPlanInfo';
import { SPRING_BASE_URL } from '$lib/utils';
import { applyGetRequestWithBearerHeader } from './http-request';

export const subscriptionPlanAPI = {
  getActiveSubscription: async (): Promise<SubscriptionPlanInfo> => {
    const url = `${SPRING_BASE_URL}/v1/subscription`;
    return (await applyGetRequestWithBearerHeader(url)).json();
  }
};
