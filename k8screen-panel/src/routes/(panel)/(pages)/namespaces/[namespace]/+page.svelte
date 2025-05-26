<script lang="ts">
  import { page } from '$app/stores';
  import * as Tabs from '$lib/components/ui/tabs/index.js';
  import Deployments from './deployments.svelte';
  import Pods from './pods.svelte';
  import Secrets from './secrets.svelte';
  import Services from './services.svelte';
  import Statefulsets from './statefulsets.svelte';

  $: namespace = $page.params.namespace;
</script>

<Tabs.Root value="deployments">
  <Tabs.List class="grid w-full grid-cols-5 space-x-2">
    <Tabs.Trigger class="hover:bg-white" value="deployments">Deployments</Tabs.Trigger>
    <Tabs.Trigger class="hover:bg-white" value="pods">Pods</Tabs.Trigger>
    <Tabs.Trigger class="hover:bg-white" value="statefulsets">StatefulSets</Tabs.Trigger>
    <Tabs.Trigger class="hover:bg-white" value="services">Services</Tabs.Trigger>
    <Tabs.Trigger class="hover:bg-white" value="secrets">Secrets</Tabs.Trigger>
  </Tabs.List>
  <Tabs.Content value="deployments">
    <Deployments namespace={namespace} />
  </Tabs.Content>
  <Tabs.Content value="pods">
    <Pods namespace={namespace} />
  </Tabs.Content>
  <Tabs.Content value="statefulsets">
    <Statefulsets namespace={namespace} />
  </Tabs.Content>
  <Tabs.Content value="services">
    <Services namespace={namespace} />
  </Tabs.Content>
  <Tabs.Content value="secrets">
    <Secrets namespace={namespace} />
  </Tabs.Content>
</Tabs.Root>
