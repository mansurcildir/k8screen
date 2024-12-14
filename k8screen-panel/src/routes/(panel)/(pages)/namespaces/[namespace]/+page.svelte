<script lang="ts">
  import { page } from "$app/stores";
  import * as Tabs from "$lib/components/ui/tabs/index.js";
  import Deployments from "./deployments.svelte";
  import Pods from "./pods.svelte";
  import Secrets from "./secrets.svelte";
  import Services from "./services.svelte";
  import Statefulsets from "./statefulsets.svelte";

  $: namespace = $page.params.namespace;

 </script>
  
 <Tabs.Root value="deployments">
  <Tabs.List class="grid w-full grid-cols-5">
   <Tabs.Trigger value="deployments">Deployments</Tabs.Trigger>
   <Tabs.Trigger value="pods">Pods</Tabs.Trigger>
   <Tabs.Trigger value="statefulsets">StatefulSets</Tabs.Trigger>
   <Tabs.Trigger value="services">Services</Tabs.Trigger>
   <Tabs.Trigger value="secrets">Secrets</Tabs.Trigger>
  </Tabs.List>
  <Tabs.Content value="deployments">
    <Deployments {namespace}/>
  </Tabs.Content>
  <Tabs.Content value="pods">
    <Pods {namespace}/>
  </Tabs.Content>
  <Tabs.Content value="statefulsets">
    <Statefulsets {namespace}/>
  </Tabs.Content>
  <Tabs.Content value="services">
    <Services {namespace}/>
  </Tabs.Content>
  <Tabs.Content value="secrets">
    <Secrets {namespace}/>
  </Tabs.Content>
</Tabs.Root>
