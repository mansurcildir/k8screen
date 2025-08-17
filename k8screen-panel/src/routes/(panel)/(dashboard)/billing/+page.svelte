<script lang="ts">
  import Button from '$lib/components/ui/button/button.svelte';
  import * as Card from '$lib/components/ui/card/index.js';
  import type { SubscriptionPlanInfo } from '$lib/model/subscription/SubscriptionPlanInfo';
  import { stripeAPI } from '$lib/service/stripe-service';
  import { subscriptionPlanAPI } from '$lib/service/subscription-plan-service';
  import { toastService } from '$lib/service/toast-service';
  import { onMount } from 'svelte';

  let activeSubscription: SubscriptionPlanInfo;

  onMount(() => getActiveSubscription());

  const getActiveSubscription = () => {
    subscriptionPlanAPI.getActiveSubscription().then((res) => {
      activeSubscription = res.data;
    });
  };

  const redirectToStripePanel = async () => {
    stripeAPI.getPanelSession().then((res) => {
      if(res.data) {
        window.location.href = res.data;
      }
    }).catch((err) => {
        toastService.show(err.message, 'error');
      });
  };
</script>

<div class="mx-auto grid grid-cols-1 gap-4 sm:grid-cols-2 md:w-[600px] lg:w-[700px] lg:grid-cols-3 xl:w-[975px]">
  <Card.Root
    class="w-full max-w-[400px] bg-secondary hover:border hover:border-primary {activeSubscription?.name === 'Free' &&
      'border border-primary'}"
  >
    <Card.Header class="mb-4">
      <Card.Title>Free (Monthly)</Card.Title>
      <Card.Description class="text-2xl">$0</Card.Description>
    </Card.Header>
    <Card.Content class="h-[200px] border-t-[1px] border-background text-sm">• 1 Config File</Card.Content>
    <Card.Footer class="flex-col gap-2">
      <Button onclick={redirectToStripePanel} class="w-full">
        {#if activeSubscription?.name === 'Free'}
          Current Plan
        {:else}
          Subscribe
        {/if}
      </Button>
    </Card.Footer>
  </Card.Root>

  <Card.Root
    class="w-full max-w-[400px] bg-secondary hover:border hover:border-primary {activeSubscription?.name ===
      'Standard' && 'border border-primary'}"
  >
    <Card.Header class="mb-4">
      <Card.Title>Standard (Monthly)</Card.Title>
      <Card.Description class="text-2xl">$5</Card.Description>
    </Card.Header>
    <Card.Content class="h-[200px] border-t-[1px] border-background text-sm">• 5 Config File</Card.Content>
    <Card.Footer class="flex-col gap-2">
      <Button onclick={redirectToStripePanel} class="w-full">
        {#if activeSubscription?.name === 'Standard'}
          Current Plan
        {:else}
          Subscribe
        {/if}
      </Button>
    </Card.Footer>
  </Card.Root>

  <Card.Root
    class="w-full max-w-[400px] bg-secondary hover:border hover:border-primary {activeSubscription?.name ===
      'Premium' && 'border border-primary'}"
  >
    <Card.Header class="mb-4">
      <Card.Title>Premium (Monthly)</Card.Title>
      <Card.Description class="text-2xl">$50</Card.Description>
    </Card.Header>
    <Card.Content class="h-[200px] border-t-[1px] border-background text-sm">• 50 Config File</Card.Content>
    <Card.Footer class="flex-col gap-2">
      <Button onclick={redirectToStripePanel} class="w-full">
        {#if activeSubscription?.name === 'Premium'}
          Current Plan
        {:else}
          Subscribe
        {/if}
      </Button>
    </Card.Footer>
  </Card.Root>
</div>
