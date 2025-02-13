<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import * as Table from '$lib/components/ui/table';
  import type { Pod } from '$lib/model/Pod';
  import { podAPI } from '$lib/service/pod-service';
  import Button from '$lib/components/ui/button/button.svelte';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import Terminal from '$lib/components/terminal.svelte';
  import * as yaml from 'yaml';
  import { Status } from '$lib/model/enum';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import Pagination from '$lib/components/pagination.svelte';
  import { Badge } from '$lib/components/ui/badge/index.js';
  import { pods, getAllPods, loadingPod } from '$lib/store';
  import { userAPI } from '$lib/service/user-service';

  export let namespace;

  let size: number = 5;
  let k8sItem: string = '';
  let details: string;
  let logs: string = '';
  let paginated: Pod[] = [];
  let open: boolean;

  $: if (namespace) {
    getAllPods(namespace);
  }

  const getDetails = async (): Promise<string> => {
    open = true;
    details = await podAPI.getPodDetails(namespace, k8sItem);
    return details;
  };

  const getLogs = async (): Promise<string> => {
    open = true;

    const user = await userAPI.getProfile();
    if (user && user.id) {
      const wsUrl = `ws://localhost:8080/ws/logs?namespace=${namespace}&podName=${k8sItem}&userId=${user.id}`;
      return wsUrl;
    }
    return '';
  };

  const updateItem = async () => {
    details = await podAPI.updatePod(namespace, k8sItem, yaml.parse(k8sItem));
    return details;
  };

  const getExec = async (): Promise<string> => {
    open = true;

    const user = await userAPI.getProfile();
    if (user && user.id) {
      const wsUrl = `ws://localhost:8080/ws/exec?namespace=${namespace}&podName=${k8sItem}&userId=${user.id}`;
      return wsUrl;
    }
    return '';
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
                k8sItem = pod.name;
                open = true;
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
                          k8sItem = pod.name;
                          getDetails();
                        }}>View</DropdownMenu.Item
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
      getExec={getExec}
      k8sItem={k8sItem}
      details={details}
      logs={logs}
      bind:open={open}
    />
  {/if}
</div>
