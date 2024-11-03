<script lang="ts">
  import Bar from "$lib/components/bar.svelte";
  import * as Table from "$lib/components/ui/table";
  import type { Pod } from "$lib/model/Pod";
  import { podAPI } from "$lib/service/pod-service";
  import * as Collapsible from "$lib/components/ui/collapsible/index.js";
  import Spinner from "$lib/components/spinner.svelte";
  import ChevronRight from "lucide-svelte/icons/chevron-right";

  export let namespace;
  let loading = false;
  let logIsLoading = false;

  let pods: Pod[] = [];
  let currentPod: string;
  let logs: string;
  let open: boolean;

  $: if (namespace) {
    getAllPods();
  }

	const getAllPods = async () => {
		try {
      loading = true;
			pods = await podAPI.getAllPods(namespace);
		} finally  {
      loading = false;
		}
	}

	const getPodLogs = async (podName: string) => {
    logIsLoading = true;
		try {
			logs = await podAPI.getPodLogs(namespace, podName);
      currentPod = podName;
      open = true;
		} finally  {
      logIsLoading = false;
		}
	}

</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>STATUS</Table.Head>
          <Table.Head>RESTARTS</Table.Head>
          <Table.Head>AGE</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loading}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each pods as pod}
            <Table.Row on:click={() => getPodLogs(pod.name)} class="cursor-pointer">
              <Table.Cell>{pod.name}</Table.Cell>
              <Table.Cell>{pod.ready_containers}</Table.Cell>
              <Table.Cell>{pod.status}</Table.Cell>
              <Table.Cell>{pod.restarts}</Table.Cell>
              <Table.Cell>{pod.age}</Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
  </div>
  
  <Collapsible.Root class="bottom-0 left-0 right-0 group/collapsible" {open}>
    <Collapsible.Trigger>
      <div class="flex justify-center items-center">
        <ChevronRight
        class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90 size-4"
      />
      <h1>{currentPod}:</h1>
      </div>
    </Collapsible.Trigger>
    <Collapsible.Content>
      <div class="h-96 bg-black text-white log-container overflow-auto rounded-md p-5">
        {#if logIsLoading}
        <div class="flex justify-center items-center h-full">
          <Spinner />
        </div>
        {:else}
        {logs}
        {/if}
      </div>
    </Collapsible.Content>
  </Collapsible.Root>
</div>

<style>
  .log-container {
    font-family: 'Courier New', Courier, monospace; /* Monospace font */
    white-space: pre-wrap; /* Preserve line breaks */
    overflow-wrap: break-word; /* Allow long words to break */
  }
</style>