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
  import IconEllipsis from 'lucide-svelte/icons/ellipsis';
  import Pagination from '$lib/components/pagination.svelte';
  import { Badge } from '$lib/components/ui/badge/index.js';
  import { pods, getAllPods, loadingPod } from '$lib/store';
  import { userAPI } from '$lib/service/user-service';
  import { authAPI } from '$lib/service/auth-service';
  import { toastService } from '$lib/service/toast-service';

  export let namespace;

  let perPage: number = 5;
  let k8sItem: string = '';
  let details: string;
  let logs: string = '';
  let paginated: Pod[] = [];
  let open: boolean;

  $: if (namespace) {
    loadPods(namespace);
  }

  const loadPods = async (namespace: string) => {
    getAllPods(namespace)
      .then(() => load(1))
      .catch((err) => {
        toastService.show(err.message, 'error');
      });
  };

  const load = (page: number) => {
    const startIndex = (page - 1) * perPage;
    const endIndex = page * perPage;
    paginated = $pods.slice(startIndex, endIndex);
  };

  const getDetails = async (): Promise<string> => {
    open = true;
    return podAPI.getPodDetails(namespace, k8sItem).then((res) => {
      details = res.data;
      return details;
    });
  };

  const updateItem = async () => {
    return podAPI.updatePod(namespace, k8sItem, yaml.parse(k8sItem)).then((res) => {
      details = res.data;
      return details;
    });
  };

  const getLogs = async (): Promise<string> => {
    open = true;

    return authAPI.getProfile().then((res) => {
      const user = res.data;
      if (user && user.uuid) {
        const wsUrl = `ws://localhost:8080/ws/logs?namespace=${namespace}&podName=${k8sItem}&userUuid=${user.uuid}`;
        return wsUrl;
      }
      return '';
    });
  };

  const getExec = async (): Promise<string> => {
    open = true;

    return authAPI.getProfile().then((res) => {
      const user = res.data;
      if (user && user.uuid) {
        const wsUrl = `ws://localhost:8080/ws/exec?namespace=${namespace}&podName=${k8sItem}&userUuid=${user.uuid}`;
        return wsUrl;
      }
      return '';
    });
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex flex-grow flex-col justify-between gap-8 overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>READY</Table.Head>
          <Table.Head>STATUS</Table.Head>
          <Table.Head>RESTARTS</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>OPTIONS</Table.Head>
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
                  class="flex w-20 justify-center"
                  variant={pod.ready_containers < pod.total_containers ? 'destructive' : 'default'}
                >
                  {pod.ready_containers}/{pod.total_containers}
                </Badge></Table.Cell
              >
              <Table.Cell>
                <Badge
                  class="flex w-20 justify-center"
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
                    <Button class="h-auto p-2" variant="ghost">
                      <IconEllipsis />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item
                        class="cursor-pointer"
                        onclick={() => {
                          k8sItem = pod.name;
                          getDetails();
                        }}>View</DropdownMenu.Item
                      >
                      <DropdownMenu.Item class="cursor-pointer">Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <Pagination perPage={perPage} load={load} count={$pods.length} />
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
