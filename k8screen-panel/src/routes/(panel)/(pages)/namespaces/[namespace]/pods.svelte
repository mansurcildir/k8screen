<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Pod } from '$lib/model/Pod';
  import { podAPI } from '$lib/service/pod-service';
  import Button from '$lib/components/ui/button/button.svelte';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import Terminal from '$lib/components/terminal.svelte';
  import * as yaml from 'yaml';
  import { OptionTerminal, Status } from '$lib/model/enum';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import Pagination from '$lib/components/pagination.svelte';
  import { Badge } from '$lib/components/ui/badge/index.js';

  export let namespace;

  let size: number = 5;

  let loading = false;
  let loadingTable = false;
  let option: OptionTerminal;
  let details: string;
  let logs: string;
  let execRes: string;

  let pods: Pod[] = [];
  let paginated: Pod[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllPods();
  }

  const load = (pod: string) => {
    k8sItem = pod;
    open = true;
    option = OptionTerminal.DETAIL;
    getDetails();
    getLogs();
  };

  const getAllPods = async () => {
    try {
      loadingTable = true;
      pods = await podAPI.getAllPods(namespace);
    } finally {
      loadingTable = false;
    }
  };

  const getLogs = async (): Promise<string> => {
    loading = true;
    logs = await podAPI.getPodLogs(namespace, k8sItem);
    loading = false;
    return logs;
  };

  const getDetails = async (): Promise<string> => {
    loading = true;
    details = await podAPI.getPodDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (editedPod: string) => {
    try {
      loading = true;
      return podAPI.updatePod(namespace, k8sItem, yaml.parse(editedPod));
    } finally {
      loading = false;
    }
  };

  const exec = async (execReq: string): Promise<string> => {
    loading = true;
    execRes = await podAPI.exec(namespace, k8sItem, ['sh', '-c', execReq]);
    loading = false;
    return execRes;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow flex flex-col gap-8 justify-between overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>STATUS</Table.Head>
          <Table.Head>RESTARTS</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>Options</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loadingTable}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each paginated as pod}
            <Table.Row
              on:click={() => {
                load(pod.name);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{pod.name}</Table.Cell>
              <Table.Cell>{pod.ready_containers}</Table.Cell>
              <Table.Cell>
                <Badge
                  class="w-20 flex justify-center"
                  variant={pod.status.toUpperCase() == Status.FAILED ? 'destructive' : 'default'}
                >
                  {pod.status}
                </Badge>
              </Table.Cell>
              <Table.Cell>{pod.restarts}</Table.Cell>
              <Table.Cell>{pod.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="p-2 h-auto" variant="ghost">
                      <IconKebabMenu />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item onclick={() => (option = OptionTerminal.DETAIL)}>View</DropdownMenu.Item>
                      <DropdownMenu.Item onclick={() => (option = OptionTerminal.EDIT)}>Edit</DropdownMenu.Item>
                      <DropdownMenu.Item>Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <div class="mb-5">
      <Pagination bind:pageSize={size} data={pods} bind:paginated={paginated} />
    </div>
  </div>

  <Terminal
    type="pod"
    getDetails={getDetails}
    updateItem={updateItem}
    getLogs={getLogs}
    exec={exec}
    k8sItem={k8sItem}
    option={option}
    details={details}
    logs={logs}
    loading={loading}
    open={open}
  />
</div>
