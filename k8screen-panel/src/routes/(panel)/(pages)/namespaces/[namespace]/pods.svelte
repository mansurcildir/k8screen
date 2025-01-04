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
  import { pods, getAllPods, loadingPod } from '$lib/store';

  export let namespace;

  let size: number = 5;

  let loading = false;
  let option: OptionTerminal;
  let details: string;
  let logs: string;
  let execRes: string;

  let paginated: Pod[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllPods(namespace);
  }

  const getDetails = async (pod: string, opt: OptionTerminal): Promise<string> => {
    loading = true;
    open = true;
    k8sItem = pod;
    option = opt;
    details = await podAPI.getPodDetails(namespace, pod);
    loading = false;
    return details;
  };

  const getLogs = async (pod: string, opt: OptionTerminal): Promise<string> => {
    open = true;
    k8sItem = pod;
    option = opt;
    logs = await podAPI.getPodLogs(namespace, pod);
    return logs;
  };

  const updateItem = async (pod: string) => {
    loading = true;
    details = await podAPI.updatePod(namespace, pod, yaml.parse(pod));
    loading = false;
    return details;
  };

  const exec = async (req: string): Promise<string> => {
    loading = true;
    open = true;
    execRes = await podAPI.exec(namespace, k8sItem, ['sh', '-c', req]);
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
        {#if $loadingPod}
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
                getDetails(pod.name, OptionTerminal.DETAIL);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{pod.name}</Table.Cell>
              <Table.Cell>
                <Badge
                  class="w-20 flex justify-center"
                  variant={pod.ready_containers < pod.total_containers ? 'destructive' : 'default'}
                >
                  {pod.ready_containers}/{pod.total_containers}
                </Badge></Table.Cell
              >
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
                      <DropdownMenu.Item
                        onclick={() => {
                          getDetails(pod.name, OptionTerminal.DETAIL);
                        }}>View</DropdownMenu.Item
                      >
                      <DropdownMenu.Item
                        onclick={() => {
                          getDetails(pod.name, OptionTerminal.EDIT);
                        }}>Edit</DropdownMenu.Item
                      >
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
      <Pagination bind:pageSize={size} data={$pods} bind:paginated={paginated} />
    </div>
  </div>

  {#if k8sItem}
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
      bind:open={open}
    />
  {/if}
</div>
